package com.firstphone.app.data.local.entities

import androidx.room.Entity

/**
 * Per-day per-package usage in milliseconds. Composite key (epochDay, packageName).
 */
@Entity(tableName = "usage_log", primaryKeys = ["epochDay", "packageName"])
data class UsageLogEntity(
    val epochDay: Long,
    val packageName: String,
    val durationMs: Long
)
