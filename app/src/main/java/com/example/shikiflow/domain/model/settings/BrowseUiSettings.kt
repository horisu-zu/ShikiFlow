package com.example.shikiflow.domain.model.settings

import com.example.shikiflow.domain.model.sort.BrowseOrder
import com.example.shikiflow.utils.AppUiMode
import com.example.shikiflow.utils.BrowseUiMode

data class BrowseUiSettings(
    val appUiMode: AppUiMode = AppUiMode.LIST,
    val browseUiMode: BrowseUiMode = BrowseUiMode.AUTO,
    val browseOngoingOrder: BrowseOrder = BrowseOrder.Anilist.SCORE
)
