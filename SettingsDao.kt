package com.firstphone.app.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.firstphone.app.util.Constants
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object WorkScheduler {

    fun scheduleAll(context: Context) {
        val wm = WorkManager.getInstance(context)

        // Daily reset at next midnight, then every 24 hours.
        val now = LocalDateTime.now(ZoneId.systemDefault())
        val nextMidnight = LocalDate.now().plusDays(1).atStartOfDay()
        val initialDelay = Duration.between(now, nextMidnight).toMinutes().coerceAtLeast(1L)

        val dailyReset = PeriodicWorkRequestBuilder<DailyResetWorker>(Duration.ofDays(1))
            .setInitialDelay(Duration.ofMinutes(initialDelay))
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.NOT_REQUIRED).build())
            .build()

        wm.enqueueUniquePeriodicWork(
            Constants.WORK_DAILY_RESET,
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyReset
        )

        // Usage sync periodic fallback (every 15 minutes)
        val usageSync = PeriodicWorkRequestBuilder<UsageSyncWorker>(Duration.ofMinutes(15))
            .build()

        wm.enqueueUniquePeriodicWork(
            Constants.WORK_USAGE_TRACK,
            ExistingPeriodicWorkPolicy.UPDATE,
            usageSync
        )
    }

    @Suppress("unused")
    private fun unusedRef(): LocalTime = LocalTime.MIDNIGHT // keep symbol import for clarity
}
