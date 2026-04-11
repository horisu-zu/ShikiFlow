package com.example.shikiflow.utils

import android.content.res.Resources
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.FileSize
import com.example.shikiflow.domain.model.track.Date as DomainDate
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

    fun DomainDate.format(locale: Locale = Locale.getDefault()): String? {
        if(day == null && month == null && year == null) return null

        val currentYear = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()).year

        val pattern = buildString {
            if (day != null) append("dd ")
            if (month != null) append("MMM ")
            if ((year != null && year != currentYear) || month == null || day == null) append("yyyy")
        }.trim()

        val safeDate = java.time.LocalDate.of(
            year ?: currentYear,
            month ?: 1,
            day ?: 1
        )

        return DateTimeFormatter.ofPattern(pattern, locale).format(safeDate)
    }

    fun convertInstantToString(resources: Resources, instant: Instant): String {
        val currentTimeInstant = Clock.System.now()
        val duration = currentTimeInstant - instant
        val diffMillis = duration.inWholeMilliseconds

        return when {
            diffMillis < 60000 -> {
                val seconds = duration.inWholeSeconds
                resources.getQuantityString(R.plurals.seconds_ago, seconds.toInt(), seconds)
            }
            diffMillis < 3600000 -> {
                val minutes = duration.inWholeMinutes
                resources.getQuantityString(R.plurals.minutes_ago, minutes.toInt(), minutes)
            }
            diffMillis < 86400000 -> {
                val hours = duration.inWholeHours
                resources.getQuantityString(R.plurals.hours_ago, hours.toInt(), hours)
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