package com.example.shikiflow.domain.model.settings

import com.example.shikiflow.domain.model.tracks.MediaType

data class Settings(
    val serviceUpdateState: Boolean = true,
    val appUiMode: AppUiMode = AppUiMode.LIST,
    val browseUiMode: BrowseUiMode = BrowseUiMode.AUTO,
    val trackMode: MediaType = MediaType.ANIME
)
