package com.example.shikiflow.utils

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.example.graphql.type.AnimeRatingEnum
import com.example.shikiflow.R
import com.example.shikiflow.data.mapper.UserRateStatusConstants
import com.example.shikiflow.data.mapper.UserRateStatusConstants.getStatusOrder
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.data.tracks.UserRate
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object Converter {
    fun formatInstant(
        lastSeenInstant: Instant?,
        includeTime: Boolean = false
    ): String {
        return lastSeenInstant?.let { instant ->
            val date = Date(instant.toEpochMilliseconds())
            val currentYear = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).year
            val instantYear = instant
                .toLocalDateTime(TimeZone.currentSystemDefault()).year

            val pattern = buildString {
                append("dd MMM")
                if (instantYear != currentYear) {
                    append(" yyyy")
                }
                if (includeTime) {
                    append(", HH:mm")
                }
            }

            SimpleDateFormat(pattern, Locale.getDefault()).format(date)
        } ?: "Not available"
    }

    fun formatDate(
        date: LocalDate,
        includeTime: Boolean = false,
        locale: Locale = Locale.getDefault()
    ): String {
        return date.let { date ->
            val currentYear = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).year
            val instantYear = date.year

            val pattern = buildString {
                append ("dd MMM")
                if (instantYear != currentYear) {
                    append(" yyyy")
                }
                if (includeTime) {
                    append(", HH:mm")
                }
            }

            val formatter = DateTimeFormatter.ofPattern(pattern, locale)
            return date.toJavaLocalDate().format(formatter)
        }
    }

    fun convertInstantToString(context: Context, lastSeenInstant: Instant?): String {
        val currentTimeInstant = Clock.System.now()
        val duration = currentTimeInstant - lastSeenInstant!!
        val diffMillis = duration.inWholeMilliseconds

        return when {
            diffMillis < 60000 -> {
                context.getString(R.string.status_now)
            }

            diffMillis < 3600000 -> {
                val minutes = duration.inWholeMinutes
                if (minutes == 1L) {
                    context.getString(R.string.status_minute_ago, minutes)
                } else {
                    context.getString(R.string.status_minutes_ago, minutes)
                }
            }

            diffMillis < 86400000 -> {
                val hours = duration.inWholeHours
                when (hours) {
                    1L -> context.getString(R.string.status_hour_ago, hours)
                    in 2..4 -> context.getString(R.string.status_hours_ago, hours)
                    else -> context.getString(R.string.status_hours_ago_v2, hours)
                }
            }

            else -> {
                val date = Date(lastSeenInstant.toEpochMilliseconds())
                val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val formattedDate = formatter.format(date)

                context.getString(R.string.status_days, formattedDate)
            }
        }
    }

    fun convertStatus(status: String): String {
        return when (status) {
            "watching" -> "Watching"
            "completed" -> "Completed"
            "on_hold" -> "On Hold"
            "dropped" -> "Dropped"
            "planned" -> "Planned"
            "rewatching" -> "Rewatching"
            "reading" -> "Reading"
            "rereading" -> "Rereading"
            else -> "Unknown"
        }
    }

    fun convertRatingToString(rating: AnimeRatingEnum?): String {
        return when(rating) {
            AnimeRatingEnum.none -> "None"
            AnimeRatingEnum.g -> "G"
            AnimeRatingEnum.pg -> "PG"
            AnimeRatingEnum.pg_13 -> "PG-13"
            AnimeRatingEnum.r -> "R-17"
            AnimeRatingEnum.r_plus -> "R+"
            AnimeRatingEnum.rx -> "Rx"
            else -> "Unknown"
        }
    }

    fun List<UserRate?>.groupAndSortByStatus(
        contentType: MediaType
    ): Map<String, Int> {
        val order = getStatusOrder(contentType)

        return this
            .groupBy { getStatusKey(contentType, it?.status ?: "unknown") }
            .mapValues { it.value.size }
            .toSortedMap(compareBy { order.indexOf(it) })
            .mapKeys { convertStatus(it.key) }
    }

    private fun getStatusKey(mediaType: MediaType, status: String): String {
        return getStatusOrder(mediaType).firstOrNull { it == status }
            ?: UserRateStatusConstants.convertStatus(status)
    }

    fun formatText(
        text: String,
        linkColor: Color
    ): AnnotatedString {
        val builder = AnnotatedString.Builder()
        formatRecursive(text, linkColor, builder)
        return builder.toAnnotatedString()
    }

    private fun formatRecursive(
        text: String,
        linkColor: Color,
        builder: AnnotatedString.Builder
    ) {
        val regex = Regex("(\\[(\\w+)(?:=(\\w+))?](.*?)\\[/\\2])|(\\[br])|(\\r\\n)")

        var lastIndex = 0

        regex.findAll(text).forEach { matchResult ->
            val fullMatch = matchResult.value
            val start = matchResult.range.first
            val end = matchResult.range.last + 1

            if (lastIndex < start) {
                builder.append(text.substring(lastIndex, start))
            }

            when {
                matchResult.groups[1]?.value != null -> {
                    val tagName = matchResult.groups[2]?.value ?: ""
                    val param = matchResult.groups[3]?.value ?: ""
                    val content = matchResult.groups[4]?.value ?: ""

                    if (param.isNotEmpty()) {
                        val annotationTag = when (tagName) {
                            "character" -> "CHARACTER_ID"
                            "spoiler" -> "SPOILER"
                            else -> "LINK"
                        }
                        val annotationValue = when (tagName) {
                            "character" -> param
                            "spoiler" -> param
                            else -> "$tagName:$param"
                        }

                        builder.pushStringAnnotation(tag = annotationTag, annotation = annotationValue)
                        builder.withStyle(SpanStyle(color = linkColor)) {
                            formatRecursive(content, linkColor, builder)
                        }
                        builder.pop()

                    } else {
                        when (tagName.lowercase()) {
                            "i" -> builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { formatRecursive(content, linkColor, builder) }
                            "b" -> builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { formatRecursive(content, linkColor, builder) }
                            "u" -> builder.withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { formatRecursive(content, linkColor, builder) }
                            "s" -> builder.withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { formatRecursive(content, linkColor, builder) }
                            else -> {
                                formatRecursive(content, linkColor, builder)
                            }
                        }
                    }
                }
                matchResult.groups[5]?.value != null || matchResult.groups[6]?.value != null -> {
                    builder.append("\n")
                }
                else -> {
                    builder.append(fullMatch)
                }
            }

            lastIndex = end
        }

        if (lastIndex < text.length) {
            builder.append(text.substring(lastIndex))
        }
    }
}