package com.firstphone.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.firstphone.app.data.local.entities.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings WHERE id = 1")
    fun observe(): Flow<SettingsEntity?>

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun get(): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: SettingsEntity)

    @Query("UPDATE settings SET usedTodayMs = :usedMs, currentEpochDay = :day WHERE id = 1")
    suspend fun updateUsage(usedMs: Long, day: Long)

    @Query("UPDATE settings SET usedTodayMs = 0, currentEpochDay = :day, overrideExtraMin = 0, overrideEpochDay = 0 WHERE id = 1")
    suspend fun resetForNewDay(day: Long)

    @Query("UPDATE settings SET failedAttempts = :attempts, lockoutUntilMs = :lockoutUntil WHERE id = 1")
    suspend fun updatePinAttempts(attempts: Int, lockoutUntil: Long)
}
