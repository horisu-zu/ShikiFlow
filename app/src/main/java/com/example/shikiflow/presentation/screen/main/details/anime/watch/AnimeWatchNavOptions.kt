package com.example.shikiflow.presentation.screen.main.details.anime.watch

import com.example.shikiflow.presentation.screen.MainNavOptions

interface AnimeWatchNavOptions : MainNavOptions {
    fun navigateToEpisodeSelection(link: String)
    fun navigateToEpisode(hlsUrl: String)
}