package com.example.shikiflow.domain.model.userrate

import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.anime.ShortAnime
import com.example.shikiflow.domain.model.manga.ShortManga
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.serialization.Serializable

@Serializable
sealed class ShortUserRate {
    abstract val score: Int
    abstract val status: UserRateStatusEnum

    @Serializable
    data class ShortAnimeRate(
        override val score: Int,
        override val status: UserRateStatusEnum,
        val anime: ShortAnime,
    ) : ShortUserRate()

    @Serializable
    data class ShortMangaRate(
        override val score: Int,
        override val status: UserRateStatusEnum,
        val manga: ShortManga,
    ) : ShortUserRate()

    companion object {
        fun ShortUserRate.getMediaId(): Long = when(this) {
            is ShortAnimeRate -> anime.id
            is ShortMangaRate -> manga.id
        }

        fun ShortUserRate.getMediaTitle(): String = when(this) {
            is ShortAnimeRate -> anime.name
            is ShortMangaRate -> manga.name
        }

        fun ShortUserRate.getImageUrl(): String? = when(this) {
            is ShortAnimeRate -> "${BuildConfig.BASE_URL}${anime.image.x96}"
            is ShortMangaRate -> "${BuildConfig.BASE_URL}${manga.image.x96}"
        }
    }
}