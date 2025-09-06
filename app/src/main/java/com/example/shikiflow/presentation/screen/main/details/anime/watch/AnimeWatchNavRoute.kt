package com.example.shikiflow.presentation.screen.main.details.anime.watch

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AnimeWatchNavRoute : NavKey {
    @Serializable
    data class TranslationSelect(val shikimoriId: String) : AnimeWatchNavRoute

    @Serializable
    data class EpisodeSelection(val link: String, val episodesCount: Int) : AnimeWatchNavRoute

    @Serializable
    data class EpisodeScreen(val link: String, val serialNum: Int) : AnimeWatchNavRoute
}