package com.example.shikiflow.presentation.screen.browse

import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.serialization.Serializable

sealed interface BrowseNavRoute: NavKey {
    @Serializable
    object BrowseScreen: BrowseNavRoute

    @Serializable
    data class SideScreen(val browseType: BrowseType): BrowseNavRoute

    @Serializable
    data class Details(val mediaId: String, val mediaType: MediaType) : BrowseNavRoute
}