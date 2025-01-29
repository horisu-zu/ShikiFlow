package com.example.shikiflow.utils

import android.content.Context
import com.example.shikiflow.R
import com.example.shikiflow.data.user.UserRateContentType
import com.example.shikiflow.data.user.animeStatusOrder
import com.example.shikiflow.data.user.animeToMangaStatusMap
import com.example.shikiflow.data.user.mangaStatusOrder
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Converter {
    fun formatInstant(lastSeenInstant: Instant?): String {
        return lastSeenInstant?.let { instant ->
            val date = Date(instant.toEpochMilliseconds())
            val currentYear =
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
            val instantYear = instant.toLocalDateTime(TimeZone.currentSystemDefault()).year

            val pattern = if (instantYear == currentYear) {
                "dd MMM, HH:mm"
            } else {
                "dd MMM yyyy, HH:mm"
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
}