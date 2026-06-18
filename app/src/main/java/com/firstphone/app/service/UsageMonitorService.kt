package com.firstphone.app.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.firstphone.app.MainActivity
import com.firstphone.app.R
import com.firstphone.app.activity.BlockActivity
import com.firstphone.app.domain.usecase.UsageTracker
import com.firstphone.app.domain.usecase.VaultStatusCalculator
import com.firstphone.app.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A lightweight foreground service that periodically polls Usage Stats and writes totals
 * to Room. The Accessibility Service handles real-time blocking; this service ensures
 * accurate counters even when the foreground app is not a Vault app.
 */
@AndroidEntryPoint
class UsageMonitorService : Service() {

    @Inject lateinit var tracker: UsageTracker
    @Inject lateinit var vaultStatus: VaultStatusCalculator

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var loopJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(Constants.MONITOR_NOTIF_ID, buildNotification(), foregroundType())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (loopJob?.isActive != true) {
            loopJob = scope.launch {
                while (isActive) {
                    runCatching {
                        tracker.rolloverIfNeeded()
                        tracker.syncToday()
                        if (vaultStatus.shouldBlockNow()) {
                            // Best-effort nudge in case accessibility missed an event
                            val i = Intent(this@UsageMonitorService, BlockActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                        }
                    }
                    delay(Constants.USAGE_POLL_INTERVAL_MS)
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun foregroundType(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0

    private fun buildNotification(): Notification {
        val pi = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, Constants.CHANNEL_MONITOR)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle(getString(R.string.notif_monitor_title))
            .setContentText(getString(R.string.notif_monitor_text))
            .setOngoing(true)
            .setSilent(true)
            .setContentIntent(pi)
            .build()
    }

    companion object {
        fun startIfNeeded(context: Context) {
            val i = Intent(context, UsageMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(i)
            else context.startService(i)
        }
    }
}
