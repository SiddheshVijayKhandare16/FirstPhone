package com.firstphone.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.firstphone.app.data.repository.SettingsRepository
import com.firstphone.app.data.repository.UsageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Runs at midnight to reset Vault counters for the new day.
 */
@HiltWorker
class DailyResetWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val settings: SettingsRepository,
    private val usage: UsageRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            settings.resetForNewDay()
            usage.pruneOld()
            Result.success()
        } catch (t: Throwable) {
            Result.retry()
        }
    }
}
