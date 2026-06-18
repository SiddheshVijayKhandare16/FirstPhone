package com.firstphone.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.firstphone.app.worker.WorkScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            WorkScheduler.scheduleAll(context)
            UsageMonitorService.startIfNeeded(context)
        }
    }
}
