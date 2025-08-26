package com.example.shikiflow.domain.model.search

import com.example.graphql.type.AnimeKindEnum
import com.example.graphql.type.AnimeStatusEnum
import com.example.graphql.type.MangaKindEnum
import com.example.graphql.type.MangaStatusEnum
import com.example.shikiflow.domain.model.anime.BrowseType
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
        fun fromBrowseType(browseType: BrowseType) = when (browseType) {
            is BrowseType.AnimeBrowseType -> Anime
            is BrowseType.MangaBrowseType -> Manga
        }
    }
}