package com.firstphone.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.firstphone.app.data.local.entities.UsageLogEntity

@Dao
interface UsageLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: UsageLogEntity)

    @Query("SELECT COALESCE(SUM(durationMs),0) FROM usage_log WHERE epochDay = :day AND packageName IN (:packages)")
    suspend fun sumForDay(day: Long, packages: List<String>): Long

    @Query("SELECT * FROM usage_log WHERE epochDay = :day")
    suspend fun forDay(day: Long): List<UsageLogEntity>

    @Query("DELETE FROM usage_log WHERE epochDay < :day")
    suspend fun pruneBefore(day: Long)
}
