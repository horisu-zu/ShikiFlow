package com.example.shikiflow.data.mapper.common

import com.example.graphql.shikimori.type.AnimeRatingEnum
import com.example.shikiflow.domain.model.media_details.MediaAgeRating

object RatingMapper {
    fun AnimeRatingEnum.toDomain(): MediaAgeRating {
        return when(this) {
            AnimeRatingEnum.none -> MediaAgeRating.NONE
            AnimeRatingEnum.g -> MediaAgeRating.G
            AnimeRatingEnum.pg -> MediaAgeRating.PG
            AnimeRatingEnum.pg_13 -> MediaAgeRating.PG_13
            AnimeRatingEnum.r -> MediaAgeRating.R_17
            AnimeRatingEnum.r_plus -> MediaAgeRating.R_PLUS
            AnimeRatingEnum.rx -> MediaAgeRating.RX
            AnimeRatingEnum.UNKNOWN__ -> MediaAgeRating.UNKNOWN
        }
    }

    fun MediaAgeRating.toShikiRating(): AnimeRatingEnum {
        return when(this) {
            MediaAgeRating.RX -> AnimeRatingEnum.rx
            MediaAgeRating.R_PLUS -> AnimeRatingEnum.r_plus
            MediaAgeRating.R_17 -> AnimeRatingEnum.r
            MediaAgeRating.PG_13 -> AnimeRatingEnum.pg_13
            MediaAgeRating.PG -> AnimeRatingEnum.pg
            MediaAgeRating.G -> AnimeRatingEnum.g
            MediaAgeRating.NONE -> AnimeRatingEnum.none
            MediaAgeRating.UNKNOWN -> AnimeRatingEnum.UNKNOWN__
        }
    }
}