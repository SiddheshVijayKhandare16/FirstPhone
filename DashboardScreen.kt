package com.firstphone.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Single-row table holding global user/parent preferences.
 * Always uses id = 1.
 */
@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,

    // PIN
    val pinHash: String? = null,
    val pinSalt: String? = null,
    val failedAttempts: Int = 0,
    val lockoutUntilMs: Long = 0L,

    // Daily limits in minutes
    val weekdayLimitMin: Int = 18,
    val saturdayLimitMin: Int = 60, // -1 = same as weekday, Int.MAX_VALUE = unlimited
    val sundayLimitMin: Int = 60,

    // Vacation mode
    val vacationStartEpochDay: Long = 0L,
    val vacationEndEpochDay: Long = 0L,
    val vacationLimitMin: Int = 0, // Int.MAX_VALUE = unlimited

    // Override (applies only on a specific epoch day)
    val overrideEpochDay: Long = 0L,
    val overrideExtraMin: Int = 0, // Int.MAX_VALUE = unlimited for that day

    // Setup flags
    val isSetupComplete: Boolean = false,

    // Tracks current epoch-day so we can detect midnight rollover lazily
    val currentEpochDay: Long = 0L,
    val usedTodayMs: Long = 0L
)
