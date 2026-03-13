package com.example.shikiflow.domain.model.settings

import com.example.shikiflow.domain.model.track.MainTrackMode

data class Settings(
    val appUiMode: AppUiMode = AppUiMode.LIST,
    val browseUiMode: BrowseUiMode = BrowseUiMode.AUTO,
    val trackMode: MainTrackMode = MainTrackMode.ANIME
)
