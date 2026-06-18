package com.firstphone.app.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

object ScheduleHelper {

    fun todayEpochDay(): Long = LocalDate.now(ZoneId.systemDefault()).toEpochDay()

    /**
     * Returns the effective Vault limit in minutes for [day], honoring vacation > weekend > weekday.
     * Returns Int.MAX_VALUE for "unlimited".
     */
    fun limitForDay(
        day: LocalDate,
        weekdayLimit: Int,
        saturdayLimit: Int,
        sundayLimit: Int,
        vacationStart: Long,
        vacationEnd: Long,
        vacationLimit: Int
    ): Int {
        val epoch = day.toEpochDay()
        if (vacationStart in 1..vacationEnd && epoch in vacationStart..vacationEnd) {
            return vacationLimit
        }
        return when (day.dayOfWeek) {
            DayOfWeek.SATURDAY -> if (saturdayLimit == -1) weekdayLimit else saturdayLimit
            DayOfWeek.SUNDAY -> if (sundayLimit == -1) weekdayLimit else sundayLimit
            else -> weekdayLimit
        }
    }

    fun limitToday(
        weekdayLimit: Int,
        saturdayLimit: Int,
        sundayLimit: Int,
        vacationStart: Long,
        vacationEnd: Long,
        vacationLimit: Int
    ): Int = limitForDay(
        LocalDate.now(ZoneId.systemDefault()),
        weekdayLimit, saturdayLimit, sundayLimit,
        vacationStart, vacationEnd, vacationLimit
    )

    fun formatTime(ms: Long): String {
        if (ms <= 0L) return "0m 0s"
        val totalSeconds = ms / 1000L
        val m = totalSeconds / 60L
        val s = totalSeconds % 60L
        return "${m}m ${s}s"
    }

    fun formatMinutes(min: Int): String {
        if (min == Int.MAX_VALUE) return "Unlimited"
        if (min <= 0) return "0 minutes"
        return "$min minutes"
    }
}
