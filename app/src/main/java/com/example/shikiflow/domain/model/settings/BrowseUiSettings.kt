package com.example.shikiflow.domain.model.settings

import com.example.shikiflow.utils.AppUiMode
import com.example.shikiflow.utils.BrowseOngoingOrder
import com.example.shikiflow.utils.BrowseUiMode

data class BrowseUiSettings(
    val appUiMode: AppUiMode = AppUiMode.LIST,
    val browseUiMode: BrowseUiMode = BrowseUiMode.AUTO,
    val browseOngoingOrder: BrowseOngoingOrder = BrowseOngoingOrder.RANKED
)
