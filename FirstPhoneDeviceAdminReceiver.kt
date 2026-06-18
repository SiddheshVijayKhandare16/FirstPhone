package com.firstphone.app.domain.usecase

import com.firstphone.app.data.local.entities.SettingsEntity
import com.firstphone.app.data.repository.AppRepository
import com.firstphone.app.data.repository.SettingsRepository
import com.firstphone.app.data.repository.UsageRepository
import com.firstphone.app.util.ScheduleHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultStatusCalculator @Inject constructor(
    private val settings: SettingsRepository,
    private val apps: AppRepository,
    private val usage: UsageRepository
) {

    /**
     * Returns whether the user is currently over the effective limit for the day,
     * which means any Vault app should be blocked.
     */
    suspend fun shouldBlockNow(): Boolean {
        val s = settings.get()
        // Edge case: setup not finished
        if (!s.isSetupComplete) return false
        val vault = apps.getVaultPackages()
        if (vault.isEmpty()) return false

        val effective = effectiveLimitMs(s)
        if (effective == Long.MAX_VALUE) return false

        val used = usage.sumToday(vault).coerceAtLeast(s.usedTodayMs)
        return used >= effective
    }

    fun effectiveLimitMs(s: SettingsEntity): Long {
        val baseLimit = ScheduleHelper.limitToday(
            s.weekdayLimitMin, s.saturdayLimitMin, s.sundayLimitMin,
            s.vacationStartEpochDay, s.vacationEndEpochDay, s.vacationLimitMin
        )
        val today = ScheduleHelper.todayEpochDay()
        val override = if (s.overrideEpochDay == today) s.overrideExtraMin else 0
        val effectiveMin = when {
            baseLimit == Int.MAX_VALUE || override == Int.MAX_VALUE -> Int.MAX_VALUE
            else -> baseLimit + override
        }
        return if (effectiveMin == Int.MAX_VALUE) Long.MAX_VALUE else effectiveMin * 60_000L
    }
}
