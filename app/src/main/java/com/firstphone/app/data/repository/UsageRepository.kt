package com.firstphone.app.data.repository

import com.firstphone.app.data.local.dao.UsageLogDao
import com.firstphone.app.data.local.entities.UsageLogEntity
import com.firstphone.app.util.ScheduleHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageRepository @Inject constructor(
    private val dao: UsageLogDao
) {
    suspend fun recordUsage(packageName: String, durationMs: Long) {
        if (durationMs <= 0L) return
        dao.upsert(UsageLogEntity(ScheduleHelper.todayEpochDay(), packageName, durationMs))
    }

    suspend fun sumToday(vaultPackages: List<String>): Long {
        if (vaultPackages.isEmpty()) return 0L
        return dao.sumForDay(ScheduleHelper.todayEpochDay(), vaultPackages)
    }

    suspend fun pruneOld(retentionDays: Int = 14) {
        dao.pruneBefore(ScheduleHelper.todayEpochDay() - retentionDays)
    }
}
