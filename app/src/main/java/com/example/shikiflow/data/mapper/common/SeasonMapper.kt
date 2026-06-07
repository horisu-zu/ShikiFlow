package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.type.MediaSeason as AnilistMediaSeason
import com.example.shikiflow.domain.model.media_details.MediaSeason
import com.example.shikiflow.domain.model.media_details.MediaSeasonEnum

object SeasonMapper {
    fun parseMediaSeason(year: Int, season: AnilistMediaSeason): MediaSeason {
        return MediaSeason(
            seasonEnum = MediaSeasonEnum.valueOf(season.name),
            year = year
        )
    }

    fun MediaSeasonEnum.toAnilistSeason(): AnilistMediaSeason {
        return when(this) {
            MediaSeasonEnum.WINTER -> AnilistMediaSeason.WINTER
            MediaSeasonEnum.SPRING -> AnilistMediaSeason.SPRING
            MediaSeasonEnum.SUMMER -> AnilistMediaSeason.SUMMER
            MediaSeasonEnum.FALL -> AnilistMediaSeason.FALL
        }
    }

    fun MediaSeason.toShikiSeason(): String? {
        return if(seasonEnum != null || year != null) {
            buildString {
                seasonEnum?.let { seasonEnum ->
                    append(seasonEnum.name.lowercase())
                }
                if(seasonEnum != null && year != null) {
                    append("_")
                }
                year?.let {
                    append(year)
                }
            }
        } else null
    }
}