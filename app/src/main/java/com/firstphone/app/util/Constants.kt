package com.firstphone.app.util

object Constants {
    const val DATABASE_NAME = "first_phone.db"

    const val CHANNEL_MONITOR = "first_phone_monitor"
    const val MONITOR_NOTIF_ID = 4201

    // WorkManager unique names
    const val WORK_DAILY_RESET = "first_phone_daily_reset"
    const val WORK_USAGE_TRACK = "first_phone_usage_track"

    // Default daily Vault limit in minutes
    const val DEFAULT_DAILY_LIMIT_MIN = 18

    // Tamper / PIN lockout
    const val MAX_PIN_ATTEMPTS = 3
    const val PIN_LOCKOUT_MS = 15L * 60L * 1000L

    // Usage poll interval (ms) used by the foreground monitor
    const val USAGE_POLL_INTERVAL_MS = 5_000L

    // Time-limit options (minutes)
    val DAILY_LIMIT_OPTIONS = listOf(18, 30, 45, 60)
    val WEEKEND_LIMIT_OPTIONS = listOf(-1, 30, 45, 60, Int.MAX_VALUE) // -1 = same as weekday, MAX_VALUE = unlimited
    val VACATION_LIMIT_OPTIONS = listOf(30, 60, 90, Int.MAX_VALUE)
    val OVERRIDE_OPTIONS = listOf(15, 30, 60, Int.MAX_VALUE) // minutes to add today
}
