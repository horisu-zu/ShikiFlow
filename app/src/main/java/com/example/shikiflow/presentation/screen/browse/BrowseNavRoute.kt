package com.example.shikiflow.presentation.screen.browse

import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.tracks.MediaType
import kotlinx.serialization.Serializable

sealed interface BrowseNavRoute: NavKey {
    @Serializable
    object BrowseScreen: BrowseNavRoute

    @Serializable
    data class SideScreen(val browseType: BrowseType): BrowseNavRoute

    @Serializable
    data class AnimeDetails(val id: String) : BrowseNavRoute

    @Serializable
    data class MangaDetails(val id: String) : BrowseNavRoute

    @Serializable
    data class CharacterDetails(val characterId: String) : BrowseNavRoute

    @Serializable
    data class SimilarPage(val id: String, val title: String, val mediaType: MediaType) : BrowseNavRoute
}