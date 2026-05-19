package com.example.shikiflow.presentation.common.mappers

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import com.example.shikiflow.R
import com.example.shikiflow.utils.DateUtils.isInCurrentWeek
import com.example.shikiflow.utils.DateUtils.timeDifference
import kotlinx.datetime.DayOfWeek
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

object DateMapper {
    fun DayOfWeek.displayValue(): Int {
        return when(this) {
            DayOfWeek.MONDAY -> R.string.day_of_week_monday
            DayOfWeek.TUESDAY -> R.string.day_of_week_tuesday
            DayOfWeek.WEDNESDAY -> R.string.day_of_week_wednesday
            DayOfWeek.THURSDAY -> R.string.day_of_week_thursday
            DayOfWeek.FRIDAY -> R.string.day_of_week_friday
            DayOfWeek.SATURDAY -> R.string.day_of_week_saturday
            DayOfWeek.SUNDAY -> R.string.day_of_week_sunday
        }
    }

    fun Instant.isAiring(startDate: Instant, episodeDuration: Duration): Boolean {
        val untilAiring = this.timeDifference()
        val airedThisWeek = !this.isInCurrentWeek() && !startDate.isInCurrentWeek()

        return if (airedThisWeek) {
            untilAiring.minus(7.days) in -episodeDuration..Duration.ZERO
        } else false
    }

    fun Duration.toCountdownString(airingAt: Instant): String {
        val episodeEndsAt = airingAt - 7.days + this
        val timeLeft = episodeEndsAt
            .timeDifference()
            .coerceAtLeast(Duration.ZERO)

        val hours = timeLeft.inWholeHours
        val minutes = timeLeft.inWholeMinutes % 60
        val seconds = timeLeft.inWholeSeconds % 60

        return if (hours > 0 || hours < -12) {
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
        }
    }

    @Composable
    fun Instant.untilNextEpisode(): String {
        val untilAiring = this.timeDifference()

        val days = untilAiring.inWholeDays
        val hours = untilAiring.inWholeHours % 24
        val minutes = untilAiring.inWholeMinutes % 60

        val timer = when {
            days > 0 -> buildString {
                append(pluralStringResource(R.plurals.days, days.toInt(), days))
                append(", ")
                append(pluralStringResource(R.plurals.hours, hours.toInt(), hours))
            }
            hours > 0 -> buildString {
                append(pluralStringResource(R.plurals.hours, hours.toInt(), hours))
                append(", ")
                append(pluralStringResource(R.plurals.minutes, minutes.toInt(), minutes))
            }
            minutes > 0 -> pluralStringResource(R.plurals.minutes, minutes.toInt(), minutes)
            else -> ""
        }

        return timer
    }
}