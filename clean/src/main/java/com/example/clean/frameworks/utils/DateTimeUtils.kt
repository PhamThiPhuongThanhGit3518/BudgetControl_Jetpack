package com.example.clean.frameworks.utils

import java.time.*
import java.time.temporal.TemporalAdjusters

object DateTimeUtils {

    fun nowMillis(): Long = System.currentTimeMillis()

    fun startOfCurrentWeek(): Long {
        val today = LocalDate.now()
        val monday = today.with(DayOfWeek.MONDAY)
        return monday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun startOfCurrentMonth(): Long {
        val today = LocalDate.now().withDayOfMonth(1)
        return today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun startOfCurrentYear(): Long {
        val today = LocalDate.now().withDayOfYear(1)
        return today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun endOfToday(): Long {
        val today = LocalDate.now()
        val end = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
        return end
    }
}