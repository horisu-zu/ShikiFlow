package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.type.MediaSeason as AnilistMediaSeason
import com.example.shikiflow.domain.model.media_details.MediaSeason
import com.example.shikiflow.domain.model.media_details.MediaSeasonEnum

object SeasonMapper {
    fun String.toDomainSeason(): MediaSeason {
        val splitParts = this.split("_")

        require(splitParts.size == 2) { "Invalid Season Format: $this" }

        val season = MediaSeasonEnum.valueOf(splitParts[0].uppercase())
        val year = splitParts[1].toInt()

        return MediaSeason(
            season = season,
            year = year
        )
    }

    fun parseMediaSeason(year: Int, season: AnilistMediaSeason): MediaSeason {
        return MediaSeason(
            season = MediaSeasonEnum.valueOf(season.name),
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

    fun MediaSeason.toShikiSeason(): String {
        return "${season.name.lowercase()}_$year"
    }
}