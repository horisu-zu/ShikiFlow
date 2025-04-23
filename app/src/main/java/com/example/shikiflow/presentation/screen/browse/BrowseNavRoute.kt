package com.example.shikiflow.presentation.screen.browse

import com.example.shikiflow.data.anime.BrowseType
import kotlinx.serialization.Serializable

sealed interface BrowseNavRoute {
    @Serializable
    object BrowseScreen: BrowseNavRoute

    @Serializable
    data class SideScreen(val browseType: BrowseType): BrowseNavRoute
}