package com.example.shikiflow.data.mapper.common

import com.example.graphql.shikimori.type.AnimeRatingEnum
import com.example.shikiflow.domain.model.media_details.AgeRating

object RatingMapper {
    fun AnimeRatingEnum.toDomain(): AgeRating {
        return when(this) {
            AnimeRatingEnum.none -> AgeRating.NONE
            AnimeRatingEnum.g -> AgeRating.G
            AnimeRatingEnum.pg -> AgeRating.PG
            AnimeRatingEnum.pg_13 -> AgeRating.PG_13
            AnimeRatingEnum.r -> AgeRating.R_17
            AnimeRatingEnum.r_plus -> AgeRating.R_PLUS
            AnimeRatingEnum.rx -> AgeRating.RX
            AnimeRatingEnum.UNKNOWN__ -> AgeRating.UNKNOWN
        }
    }

    fun AgeRating.toShikiRating(): AnimeRatingEnum {
        return when(this) {
            AgeRating.RX -> AnimeRatingEnum.rx
            AgeRating.R_PLUS -> AnimeRatingEnum.r_plus
            AgeRating.R_17 -> AnimeRatingEnum.r
            AgeRating.PG_13 -> AnimeRatingEnum.pg_13
            AgeRating.PG -> AnimeRatingEnum.pg
            AgeRating.G -> AnimeRatingEnum.g
            AgeRating.NONE -> AnimeRatingEnum.none
            AgeRating.UNKNOWN -> AnimeRatingEnum.UNKNOWN__
        }
    }
}