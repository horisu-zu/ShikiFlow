package com.example.shikiflow.data.search

import com.example.graphql.type.AnimeKindEnum
import com.example.graphql.type.AnimeStatusEnum
import com.example.graphql.type.MangaKindEnum
import com.example.graphql.type.MangaStatusEnum
import com.example.shikiflow.data.tracks.MediaType
import kotlin.reflect.KClass

sealed class ContentType {
    abstract val kindEnum: KClass<out Enum<*>>
    abstract val statusEnum: KClass<out Enum<*>>

    data object Anime : ContentType() {
        override val kindEnum = AnimeKindEnum::class
        override val statusEnum = AnimeStatusEnum::class
    }

    data object Manga : ContentType() {
        override val kindEnum = MangaKindEnum::class
        override val statusEnum = MangaStatusEnum::class
    }

    companion object {
        fun fromMediaType(mediaType: MediaType?) = when (mediaType) {
            MediaType.ANIME -> Anime
            MediaType.MANGA -> Manga
            else -> null
        }
    }
}