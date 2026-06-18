package com.firstphone.app.data.repository

import com.firstphone.app.data.local.dao.SettingsDao
import com.firstphone.app.data.local.entities.SettingsEntity
import com.firstphone.app.util.Constants
import com.firstphone.app.util.PinHasher
import com.firstphone.app.util.ScheduleHelper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dao: SettingsDao
) {

    fun observe(): Flow<SettingsEntity?> = dao.observe()

    suspend fun get(): SettingsEntity {
        val current = dao.get()
        if (current != null) return current
        val fresh = SettingsEntity(currentEpochDay = ScheduleHelper.todayEpochDay())
        dao.upsert(fresh)
        return fresh
    }

    suspend fun setPin(pin: String) {
        val salt = PinHasher.newSalt()
        val hash = PinHasher.hash(pin, salt)
        val s = get().copy(pinHash = hash, pinSalt = salt, failedAttempts = 0, lockoutUntilMs = 0L)
        dao.upsert(s)
    }

    /**
     * @return Pair(success, lockoutUntilMs). lockoutUntilMs > 0 means locked.
     */
    suspend fun verifyPin(pin: String): Pair<Boolean, Long> {
        val s = get()
        val now = System.currentTimeMillis()
        if (s.lockoutUntilMs > now) return false to s.lockoutUntilMs
        val ok = s.pinHash != null && s.pinSalt != null &&
            PinHasher.verify(pin, s.pinSalt, s.pinHash)
        if (ok) {
            dao.updatePinAttempts(0, 0L)
            return true to 0L
        }
        val attempts = s.failedAttempts + 1
        val lockUntil = if (attempts >= Constants.MAX_PIN_ATTEMPTS) now + Constants.PIN_LOCKOUT_MS else 0L
        val newAttempts = if (lockUntil > 0L) 0 else attempts
        dao.updatePinAttempts(newAttempts, lockUntil)
        return false to lockUntil
    }

    suspend fun markSetupComplete() {
        val s = get()
        dao.upsert(s.copy(isSetupComplete = true))
    }

    suspend fun setWeekdayLimit(min: Int) {
        val s = get(); dao.upsert(s.copy(weekdayLimitMin = min))
    }

    suspend fun setWeekendLimits(saturday: Int, sunday: Int) {
        val s = get(); dao.upsert(s.copy(saturdayLimitMin = saturday, sundayLimitMin = sunday))
    }

    suspend fun setVacation(startEpochDay: Long, endEpochDay: Long, limitMin: Int) {
        val s = get(); dao.upsert(s.copy(
            vacationStartEpochDay = startEpochDay,
            vacationEndEpochDay = endEpochDay,
            vacationLimitMin = limitMin
        ))
    }

    suspend fun clearVacation() {
        val s = get(); dao.upsert(s.copy(
            vacationStartEpochDay = 0L, vacationEndEpochDay = 0L, vacationLimitMin = 0
        ))
    }

    suspend fun addOverride(extraMin: Int) {
        val today = ScheduleHelper.todayEpochDay()
        val s = get()
        val current = if (s.overrideEpochDay == today) s.overrideExtraMin else 0
        val newTotal = if (extraMin == Int.MAX_VALUE || current == Int.MAX_VALUE) Int.MAX_VALUE else current + extraMin
        dao.upsert(s.copy(overrideEpochDay = today, overrideExtraMin = newTotal))
    }

    suspend fun updateUsage(usedMs: Long) {
        val today = ScheduleHelper.todayEpochDay()
        dao.updateUsage(usedMs, today)
    }

    suspend fun resetForNewDay() {
        dao.resetForNewDay(ScheduleHelper.todayEpochDay())
    }
}
