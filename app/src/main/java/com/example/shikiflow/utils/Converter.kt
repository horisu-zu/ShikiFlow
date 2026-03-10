package com.example.shikiflow.utils

import android.content.res.Resources
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.FileSize
import com.example.shikiflow.domain.model.track.MediaFormat
import com.fleeksoft.ksoup.Ksoup
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.Instant

object Converter {
    fun formatInstant(
        instant: Instant,
        includeTime: Boolean = false,
        includeDayOfWeek: Boolean = false
    ): String {
        val date = Date(instant.toEpochMilliseconds())
        val currentYear = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).year
        val instantYear = instant
            .toLocalDateTime(TimeZone.currentSystemDefault()).year

        val pattern = buildString {
            if(includeDayOfWeek) {
                append("EEE, ")
            }
            append("dd MMM")
            if (instantYear != currentYear) {
                append(" yyyy")
            }
            if (includeTime && instantYear == currentYear) {
                append(" HH:mm")
            }
        }

        return SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }

    fun formatDate(
        date: LocalDate,
        includeTime: Boolean = false,
        locale: Locale = Locale.getDefault()
    ): String {
        date.let { date ->
            val currentYear = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).year
            val instantYear = date.year

            val pattern = buildString {
                append ("dd MMM")
                if (instantYear != currentYear) {
                    append(" yyyy")
                }
                if (includeTime) {
                    append(" HH:mm")
                }
            }

            val formatter = DateTimeFormatter.ofPattern(pattern, locale)
            return date.toJavaLocalDate().format(formatter)
        }
    }

    fun convertInstantToString(resources: Resources, instant: Instant): String {
        val currentTimeInstant = Clock.System.now()
        val duration = currentTimeInstant - instant
        val diffMillis = duration.inWholeMilliseconds

        return when {
            diffMillis < 3600000 -> {
                val minutes = duration.inWholeMinutes
                if (minutes == 1L) {
                    resources.getString(R.string.status_minute_ago, minutes)
                } else {
                    resources.getString(R.string.status_minutes_ago, minutes)
                }
            }
            diffMillis < 86400000 -> {
                when (val hours = duration.inWholeHours) {
                    1L -> resources.getString(R.string.status_hour_ago, hours)
                    else -> resources.getString(R.string.status_hours_ago, hours)
                }
            }
            else -> {
                val date = Date(instant.toEpochMilliseconds())
                val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                formatter.format(date)
            }
        }
    }

    fun formatFileSize(size: Double): FileSize {
        return when {
            size < 1024 -> FileSize(value = size, unit = FileSize.SizeUnit.B)
            size < 1024 * 1024 -> FileSize(value = size / 1024, unit = FileSize.SizeUnit.KB)
            size < 1024 * 1024 * 1024 -> FileSize(value = size / (1024 * 1024), unit = FileSize.SizeUnit.MB)
            else -> FileSize(value = size / (1024 * 1024 * 1024), unit = FileSize.SizeUnit.GB)
        }
    }

    fun formatDuration(durationMs: Long): String {
        if (durationMs <= 0) return "00:00"

        val totalSeconds = durationMs / 1000
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val hours = totalSeconds / 3600

        return if (hours > 0) {
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d".format(minutes, seconds)
        }
    }

    fun MediaFormat.isManga(): Boolean {
        return this in setOf(MediaFormat.MANGA, MediaFormat.MANHWA,
            MediaFormat.MANHUA, MediaFormat.ONE_SHOT, MediaFormat.DOUJIN)
    }

    fun String.toAbbreviation(maxLetters: Int = 2): String {
        val words = this.split(Regex("\\s+")).filter { it.isNotBlank() }

        return when {
            words.size == 1 -> {
                val capitals = words[0].filter { it.isUpperCase() }
                when {
                    capitals.length >= maxLetters -> capitals.take(maxLetters)
                    capitals.isNotEmpty() -> capitals + words[0]
                        .filter { it.isLetter() && !it.isUpperCase() }
                        .take(maxLetters - capitals.length)
                        .map { it.uppercaseChar() }
                        .joinToString("").uppercase()
                    else -> words[0].take(maxLetters).uppercase()
                }
            }
            else -> words.take(maxLetters).map { it.first().uppercaseChar() }.joinToString("")
        }
    }

    fun String.isHTMLStringBlank(): Boolean {
        val text = Ksoup.parse(this).text()

        return text.isBlank()
    }

    fun parseChapterNumber(chapterNumber: String): Float {
        return chapterNumber.split("-", "–", "—")
            .first()
            .trim()
            .toFloatOrNull() ?: 0f
    }
}