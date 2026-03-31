package com.example.shikiflow.presentation.screen.main.details.anime.watch

import com.example.shikiflow.presentation.screen.NavOptions
import com.example.shikiflow.presentation.screen.main.details.anime.watch.player.EpisodeMetadata

interface AnimeWatchNavOptions : NavOptions {
    fun navigateToEpisodeSelection(link: String, translationGroup: String, episodesCount: Int)
    fun navigateToEpisodeScreen(playerNavigate: EpisodeMetadata)
}