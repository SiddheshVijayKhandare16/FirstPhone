package com.firstphone.app.domain.usecase

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.firstphone.app.data.repository.AppRepository
import com.firstphone.app.data.repository.SettingsRepository
import com.firstphone.app.data.repository.UsageRepository
import com.firstphone.app.util.ScheduleHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Polls the system UsageStatsManager for foreground events and accumulates per-package usage
 * for Vault apps into Room. Designed to be called repeatedly from a foreground service or worker.
 */
@Singleton
class UsageTracker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appRepo: AppRepository,
    private val settingsRepo: SettingsRepository,
    private val usageRepo: UsageRepository
) {

    private var lastCursorMs: Long = 0L

    /**
     * Computes Vault usage since the start of today and writes the totals to Room.
     * Returns total Vault usage (ms) for today across all Vault packages.
     */
    suspend fun syncToday(): Long {
        val vaultPackages = appRepo.getVaultPackages().toSet()
        if (vaultPackages.isEmpty()) return 0L

        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val startOfDay = LocalDate.now(ZoneId.systemDefault())
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val now = System.currentTimeMillis()

        val events = usm.queryEvents(startOfDay, now)
        val totals = mutableMapOf<String, Long>()
        val openAt = mutableMapOf<String, Long>()
        val ev = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(ev)
            val pkg = ev.packageName ?: continue
            if (pkg !in vaultPackages) continue
            when (ev.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> openAt[pkg] = ev.timeStamp
                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.ACTIVITY_STOPPED -> {
                    val start = openAt.remove(pkg) ?: continue
                    val dur = (ev.timeStamp - start).coerceAtLeast(0L)
                    totals[pkg] = (totals[pkg] ?: 0L) + dur
                }
            }
        }
        // Any still-open sessions count up to "now"
        for ((pkg, start) in openAt) {
            val dur = (now - start).coerceAtLeast(0L)
            totals[pkg] = (totals[pkg] ?: 0L) + dur
        }

        var total = 0L
        for ((pkg, dur) in totals) {
            usageRepo.recordUsage(pkg, dur)
            total += dur
        }
        settingsRepo.updateUsage(total)

        lastCursorMs = now
        return total
    }

    /**
     * Handles midnight rollover by resetting daily counters if the stored epochDay is stale.
     */
    suspend fun rolloverIfNeeded() {
        val s = settingsRepo.get()
        val today = ScheduleHelper.todayEpochDay()
        if (s.currentEpochDay != today) {
            settingsRepo.resetForNewDay()
            usageRepo.pruneOld()
        }
    }
}
