package com.example.shikiflow.presentation.screen.main.details.anime.watch

import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.presentation.screen.main.details.anime.watch.player.EpisodeMetadata
import kotlinx.serialization.Serializable

sealed interface AnimeWatchNavRoute : NavKey {
    @Serializable
    data class TranslationSelect(val shikimoriId: Int) : AnimeWatchNavRoute

    @Serializable
    data class EpisodeSelection(
        val link: String,
        val translationGroup: String,
        val firstEpisode: Int,
        val lastEpisode: Int
    ) : AnimeWatchNavRoute

    @Serializable
    data class EpisodeScreen(val episodeMetadata: EpisodeMetadata) : AnimeWatchNavRoute
}