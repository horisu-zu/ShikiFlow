package com.example.shikiflow.presentation.screen

import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.data.tracks.MediaType
import kotlinx.serialization.Serializable

sealed interface MainNavRoute : NavKey {
    @Serializable
    data object Home : MainNavRoute

    @Serializable
    data object Browse : MainNavRoute

    @Serializable
    data object More : MainNavRoute
}

sealed interface MainScreenNavRoute : NavKey {
    @Serializable
    data object MainTracks : MainScreenNavRoute

    @Serializable
    data class AnimeDetails(val id: String) : MainScreenNavRoute

    @Serializable
    data class MangaDetails(val id: String) : MainScreenNavRoute

    @Serializable
    data class CharacterDetails(val characterId: String) : MainScreenNavRoute

    @Serializable
    data class SimilarPage(val id: String, val title: String, val mediaType: MediaType) : MainScreenNavRoute
}