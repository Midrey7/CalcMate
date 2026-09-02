package com.calcmate.allinonecalculator.domain

import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

data class DateDifference(val years: Int, val months: Int, val days: Int, val totalDays: Long)
object DateCalculations {
    fun difference(start: LocalDate, end: LocalDate): DateDifference { require(!end.isBefore(start)); val period = Period.between(start, end); return DateDifference(period.years, period.months, period.days, ChronoUnit.DAYS.between(start, end)) }
    fun add(date: LocalDate, years: Long = 0, months: Long = 0, days: Long = 0): LocalDate = date.plusYears(years).plusMonths(months).plusDays(days)
    fun subtract(date: LocalDate, years: Long = 0, months: Long = 0, days: Long = 0): LocalDate = date.minusYears(years).minusMonths(months).minusDays(days)
    fun daysUntilBirthday(birth: LocalDate, today: LocalDate): Long { var next = birth.withYear(today.year); if (!next.isAfter(today)) next = next.plusYears(1); return ChronoUnit.DAYS.between(today, next) }
}
