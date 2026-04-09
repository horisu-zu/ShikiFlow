package com.example.shikiflow.data.datasource.dto.media

import com.example.graphql.shikimori.type.UserRateStatusEnum
import kotlinx.serialization.Serializable

@Serializable
sealed interface ShikiShortUserRate {
    val score: Int
    val status: UserRateStatusEnum

    @Serializable
    data class ShikiShortAnimeRate(
        override val score: Int,
        override val status: UserRateStatusEnum,
        val anime: ShikiShortAnime,
    ) : ShikiShortUserRate

    @Serializable
    data class ShikiShortMangaRate(
        override val score: Int,
        override val status: UserRateStatusEnum,
        val manga: ShikiShortManga,
    ) : ShikiShortUserRate
}