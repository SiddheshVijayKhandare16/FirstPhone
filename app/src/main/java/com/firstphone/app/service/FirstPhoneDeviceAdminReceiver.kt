package com.firstphone.app.service

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent

class FirstPhoneDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        // Hook for future telemetry; intentionally empty.
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        return "Removing First Phone will disable protection. Are you sure?"
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
    }
}
