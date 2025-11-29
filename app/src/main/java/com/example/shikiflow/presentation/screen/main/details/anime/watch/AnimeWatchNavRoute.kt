package com.example.shikiflow.presentation.screen.main.details.anime.watch

import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.presentation.screen.main.details.anime.watch.player.PlayerNavigate
import kotlinx.serialization.Serializable

sealed interface AnimeWatchNavRoute : NavKey {
    @Serializable
    data class TranslationSelect(val shikimoriId: String) : AnimeWatchNavRoute

    @Serializable
    data class EpisodeSelection(
        val link: String,
        val translationGroup: String,
        val episodesCount: Int
    ) : AnimeWatchNavRoute

    @Serializable
    data class EpisodeScreen(val playerNavigate: PlayerNavigate) : AnimeWatchNavRoute
}