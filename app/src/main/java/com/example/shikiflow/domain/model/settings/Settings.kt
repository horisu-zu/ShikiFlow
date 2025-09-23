package com.example.shikiflow.domain.model.settings

import com.example.shikiflow.presentation.screen.main.MainTrackMode
import com.example.shikiflow.utils.AppUiMode
import com.example.shikiflow.utils.BrowseUiMode

data class Settings(
    val appUiMode: AppUiMode = AppUiMode.LIST,
    val browseUiMode: BrowseUiMode = BrowseUiMode.AUTO,
    val trackMode: MainTrackMode = MainTrackMode.ANIME
)
