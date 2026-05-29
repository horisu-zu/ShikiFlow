package com.example.shikiflow.domain.model.settings

import com.example.shikiflow.domain.model.sort.MediaSort

data class BrowseUiSettings(
    val appUiMode: AppUiMode? = null,
    val browseUiMode: BrowseUiMode? = null,
    val browseOngoingOrder: MediaSort? = null
)
