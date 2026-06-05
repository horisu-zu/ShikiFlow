package com.example.shikiflow.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

object DateUtils {
    const val BASE_YEAR = 1917

    fun Instant.thisWeekdayTimestamp(dayOfWeek: DayOfWeek, isEndOfDay: Boolean): Long {
        val dateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())

        val diff = dayOfWeek.ordinal - dateTime.dayOfWeek.ordinal
        val weekdayDate = dateTime.date.plus(diff, DateTimeUnit.DAY)

        return if (isEndOfDay) {
            weekdayDate
                .plus(1, DateTimeUnit.DAY)
                .atStartOfDayIn(TimeZone.currentSystemDefault())
                .minus(1, DateTimeUnit.NANOSECOND)
                .epochSeconds
        } else {
            weekdayDate
                .atStartOfDayIn(TimeZone.currentSystemDefault())
                .minus(1, DateTimeUnit.NANOSECOND)
                .epochSeconds
        }
    }

    fun Instant.timeDifference(): Duration {
        val now = Clock.System.now()
        return this - now
    }

    fun Instant.isInCurrentWeek(): Boolean {
        val timeZone = TimeZone.currentSystemDefault()

        val today = Clock.System.now().toLocalDateTime(timeZone).date
        val date = this.toLocalDateTime(timeZone).date

        val startOfWeek = today.minus(today.dayOfWeek.ordinal, DateTimeUnit.DAY)
        val endOfWeek = startOfWeek.plus(6, DateTimeUnit.DAY)

        return date in startOfWeek..endOfWeek
    }

    fun calculateDelayUntil(hour: Int, minute: Int): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val now = Clock.System.now()
        val nowLocal = now.toLocalDateTime(timeZone)

        var target = LocalDateTime(
            date = nowLocal.date,
            time = LocalTime(hour, minute, 0)
        )

        if (nowLocal >= target) {
            target = LocalDateTime(
                date = nowLocal.date.plus(1, DateTimeUnit.DAY),
                time = LocalTime(hour, minute, 0)
            )
        }

        val targetInstant = target.toInstant(timeZone)

        return (targetInstant - now).inWholeMilliseconds
    }

    fun Int.calculateDuration(): Pair<Int, Int> {
        val hours = this / 60
        val minutes = this % 60

        return hours to minutes
    }

    fun seasonYears(): List<Int> {
        val currentYear = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .year

        return ((currentYear + 1)downTo BASE_YEAR).toList()
    }
}