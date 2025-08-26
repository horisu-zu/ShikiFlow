package com.example.shikiflow.presentation.screen.main.details

import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.main.details.common.CommentsScreenMode
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

    @Serializable
    data class ExternalLinks(val mediaId: String, val mediaType: MediaType) : DetailsNavRoute

    @Serializable
    data class MangaRead(val mangaDexIds: List<String>, val title: String, val completedChapters: Int) : DetailsNavRoute

    @Serializable
    data class Comments(val screenMode: CommentsScreenMode, val id: String) : DetailsNavRoute
}