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
import com.example.shikiflow.data.tracks.UserRateContentType
import com.example.shikiflow.data.tracks.animeStatusOrder
import com.example.shikiflow.data.tracks.animeToMangaStatusMap
import com.example.shikiflow.data.tracks.mangaStatusOrder
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
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

    fun <T> List<T>.groupAndSortByStatus(
        contentType: UserRateContentType,
        statusExtractor: (T) -> String?
    ): Map<String, Int> {
        val order = when (contentType) {
            UserRateContentType.ANIME -> animeStatusOrder
            UserRateContentType.MANGA -> mangaStatusOrder
        }

        return this
            .groupBy { item ->
                val status = statusExtractor(item)?.lowercase() ?: ""
                getStatusKey(contentType, status)
            }
            .mapValues { it.value.size }
            .toList()
            .sortedBy { (status, _) -> order.indexOf(status) }
            .toMap()
    }

    private fun getStatusKey(contentType: UserRateContentType, status: String): String {
        return when (contentType) {
            UserRateContentType.ANIME -> animeStatusOrder.firstOrNull { it == status }
            UserRateContentType.MANGA -> {
                val mappedStatus = animeToMangaStatusMap[status] ?: status
                mangaStatusOrder.firstOrNull { it == mappedStatus }
            }
        } ?: "unknown"
    }

    fun formatText(
        text: String,
        linkColor: Color
    ): AnnotatedString {
        val builder = AnnotatedString.Builder()

        val regex = Regex("\\[(\\w+)(?:=(\\w+))?\\](.*?)\\[/\\1\\]")
        var lastIndex = 0

        regex.findAll(text).forEach { matchResult ->
            val tagName = matchResult.groupValues[1]
            val param = matchResult.groupValues[2]
            val content = matchResult.groupValues[3]
            val start = matchResult.range.first

            if (lastIndex < start) {
                builder.append(text.substring(lastIndex, start))
            }

            if (param.isNotEmpty()) {
                val annotationTag = when (tagName) {
                    "character" -> "CHARACTER_ID"
                    else -> "LINK"
                }
                val annotationValue = if (tagName == "character") param else "$tagName:$param"

                builder.pushStringAnnotation(tag = annotationTag, annotation = annotationValue)
                builder.withStyle(SpanStyle(color = linkColor)) {
                    builder.append(content)
                }
                builder.pop()
            } else {
                when (tagName.lowercase()) {
                    "i" -> builder.withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { builder.append(content) }
                    "b" -> builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { builder.append(content) }
                    "u" -> builder.withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { builder.append(content) }
                    "s" -> builder.withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { builder.append(content) }
                    else -> builder.append(content)
                }
            }

            lastIndex = matchResult.range.last + 1
        }

        if (lastIndex < text.length) {
            builder.append(text.substring(lastIndex))
        }

        return builder.toAnnotatedString()
    }
}