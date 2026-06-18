package com.firstphone.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "managed_apps")
data class ManagedAppEntity(
    @PrimaryKey val packageName: String,
    val label: String,
    val isAlwaysAllowed: Boolean = false,
    val isVault: Boolean = false
)
