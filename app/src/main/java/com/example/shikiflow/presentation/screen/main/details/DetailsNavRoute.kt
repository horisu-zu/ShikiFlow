package com.example.shikiflow.presentation.screen.main.details

import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.data.tracks.MediaType
import kotlinx.serialization.Serializable

sealed interface DetailsNavRoute : NavKey {
    @Serializable
    data class AnimeDetails(val id: String) : DetailsNavRoute

    @Serializable
    data class MangaDetails(val id: String) : DetailsNavRoute

    @Serializable
    data class CharacterDetails(val characterId: String) : DetailsNavRoute

    @Serializable
    data class SimilarPage(val id: String, val title: String, val mediaType: MediaType) : DetailsNavRoute
}