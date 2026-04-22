package com.example.shikiflow.domain.model.media_details

import com.example.shikiflow.domain.model.media_details.MediaSeasonEnum.FALL
import com.example.shikiflow.domain.model.media_details.MediaSeasonEnum.SPRING
import com.example.shikiflow.domain.model.media_details.MediaSeasonEnum.SUMMER
import com.example.shikiflow.domain.model.media_details.MediaSeasonEnum.WINTER
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class MediaSeason(
    val season: MediaSeasonEnum,
    val year: Int
) {
    companion object {
        fun currentSeason(): MediaSeason {
            val now = Clock.System.now()
            val date = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

            val season = when(date.month.ordinal + 1) {
                in 1..3 -> WINTER
                in 4..6 -> SPRING
                in 7..9 -> SUMMER
                else -> FALL
            }

            return MediaSeason(
                season = season,
                year = date.year
            )
        }

        fun nextSeason(): MediaSeason {
            val now = Clock.System.now()
            val date = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

            val next = when(date.month.ordinal + 1) {
                in 1..3 -> SPRING
                in 4..6 -> SUMMER
                in 7..10 -> FALL
                else -> WINTER
            }

            return MediaSeason(
                season = next,
                year = if (next == WINTER) date.year + 1 else date.year
            )
        }
    }
}

enum class MediaSeasonEnum {
    WINTER,
    SPRING,
    SUMMER,
    FALL
}
