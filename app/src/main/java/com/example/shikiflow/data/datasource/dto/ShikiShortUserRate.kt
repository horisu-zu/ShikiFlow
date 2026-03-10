package com.example.shikiflow.data.datasource.dto

import com.example.graphql.shikimori.type.UserRateStatusEnum
import com.example.shikiflow.BuildConfig
import kotlinx.serialization.Serializable

@Serializable
sealed class ShikiShortUserRate {
    abstract val score: Int
    abstract val status: UserRateStatusEnum

    @Serializable
    data class ShikiShortAnimeRate(
        override val score: Int,
        override val status: UserRateStatusEnum,
        val anime: ShikiShortAnime,
    ) : ShikiShortUserRate()

    @Serializable
    data class ShikiShortMangaRate(
        override val score: Int,
        override val status: UserRateStatusEnum,
        val manga: ShikiShortManga,
    ) : ShikiShortUserRate()

    companion object {
        fun ShikiShortUserRate.getMediaId(): Long = when(this) {
            is ShikiShortAnimeRate -> anime.id
            is ShikiShortMangaRate -> manga.id
        }

        fun ShikiShortUserRate.getMediaTitle(): String = when(this) {
            is ShikiShortAnimeRate -> anime.name
            is ShikiShortMangaRate -> manga.name
        }

        fun ShikiShortUserRate.getImageUrl(): String? = when(this) {
            is ShikiShortAnimeRate -> "${BuildConfig.SHIKI_BASE_URL}${anime.image.x96}"
            is ShikiShortMangaRate -> "${BuildConfig.SHIKI_BASE_URL}${manga.image.x96}"
        }

        fun ShikiShortUserRate.getProgress(): Int = when(this) {
            is ShikiShortAnimeRate -> anime.episodes
            is ShikiShortMangaRate -> manga.chapters
        }
    }
}