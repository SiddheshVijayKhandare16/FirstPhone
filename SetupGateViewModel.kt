package com.firstphone.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.firstphone.app.data.local.dao.ManagedAppDao
import com.firstphone.app.data.local.dao.SettingsDao
import com.firstphone.app.data.local.dao.UsageLogDao
import com.firstphone.app.data.local.entities.ManagedAppEntity
import com.firstphone.app.data.local.entities.SettingsEntity
import com.firstphone.app.data.local.entities.UsageLogEntity

@Database(
    entities = [
        SettingsEntity::class,
        ManagedAppEntity::class,
        UsageLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FirstPhoneDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
    abstract fun managedAppDao(): ManagedAppDao
    abstract fun usageLogDao(): UsageLogDao
}
