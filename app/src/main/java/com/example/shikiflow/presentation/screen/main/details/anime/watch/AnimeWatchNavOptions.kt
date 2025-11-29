package com.example.shikiflow.presentation.screen.main.details.anime.watch

import com.example.shikiflow.presentation.screen.MainNavOptions
import com.example.shikiflow.presentation.screen.main.details.anime.watch.player.PlayerNavigate

interface AnimeWatchNavOptions : MainNavOptions {
    fun navigateToEpisodeSelection(link: String, translationGroup: String, episodesCount: Int)
    fun navigateToEpisodeScreen(playerNavigate: PlayerNavigate)
}