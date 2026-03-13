package com.example.shikiflow.domain.model.settings

import com.example.shikiflow.domain.model.sort.MediaSort

data class BrowseUiSettings(
    val appUiMode: AppUiMode = AppUiMode.LIST,
    val browseUiMode: BrowseUiMode = BrowseUiMode.AUTO,
    val browseOngoingOrder: MediaSort = MediaSort.Anilist.SCORE
)
