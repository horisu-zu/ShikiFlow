package com.example.shikiflow.presentation.screen.browse

import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import kotlinx.serialization.Serializable

sealed interface BrowseNavRoute: NavKey {
    @Serializable
    object BrowseScreen: BrowseNavRoute

    @Serializable
    data class SideScreen(val browseType: BrowseType): BrowseNavRoute

    @Serializable
    data class Details(val detailsNavRoute: DetailsNavRoute) : BrowseNavRoute
}