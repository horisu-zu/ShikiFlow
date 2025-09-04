package com.example.shikiflow.presentation.screen.main.details.anime.watch

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AnimeWatchNavRoute : NavKey {
    @Serializable
    data class TranslationSelect(val title: String, val shikimoriId: String) : AnimeWatchNavRoute

    @Serializable
    data class EpisodeSelection(val link: String) : AnimeWatchNavRoute

    @Serializable
    data class EpisodeScreen(val hlsUrl: String) : AnimeWatchNavRoute
}