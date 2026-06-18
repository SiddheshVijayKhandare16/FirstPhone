package com.firstphone.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.firstphone.app.domain.usecase.UsageTracker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Periodic fallback to keep usage counters fresh even if the foreground service is killed.
 */
@HiltWorker
class UsageSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val tracker: UsageTracker
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            tracker.rolloverIfNeeded()
            tracker.syncToday()
            Result.success()
        } catch (t: Throwable) {
            Result.retry()
        }
    }
}
