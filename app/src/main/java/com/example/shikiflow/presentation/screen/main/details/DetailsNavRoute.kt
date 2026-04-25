package com.example.shikiflow.presentation.screen.main.details

import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.serialization.Serializable

@Serializable
sealed interface DetailsNavRoute : NavKey {
    @Serializable
    data class AnimeDetails(val id: Int) : DetailsNavRoute

    @Serializable
    data class MangaDetails(val id: Int) : DetailsNavRoute

    @Serializable
    data class CharacterDetails(val characterId: Int) : DetailsNavRoute

    @Serializable
    data class MediaCharacters(val mediaId: Int, val mediaTitle: String, val mediaType: MediaType) : DetailsNavRoute

    @Serializable
    data class SimilarPage(val id: Int, val title: String, val mediaType: MediaType) : DetailsNavRoute

    @Serializable
    data class ExternalLinks(val mediaId: Int, val mediaType: MediaType) : DetailsNavRoute

    @Serializable
    data class MangaRead(
        val mangaDexIds: List<String>,
        val malId: Int,
        val title: String,
        val completedChapters: Int
    ) : DetailsNavRoute

    @Serializable
    data class Threads(val mediaId: Int): DetailsNavRoute

    @Serializable
    data class Comments(
        val screenMode: CommentsScreenMode,
        val id: Int,
        val threadHeader: Thread?
    ) : DetailsNavRoute
    
    @Serializable
    data class Staff(val staffId: Int) : DetailsNavRoute

    @Serializable
    data class MediaStaff(val mediaId: Int, val mediaType: MediaType) : DetailsNavRoute

    @Serializable
    data class AnimeWatch(val title: String, val shikimoriId: Int, val completedEpisodes: Int) : DetailsNavRoute

    @Serializable
    data class Studio(val id: Int, val studioName: String) : DetailsNavRoute

    @Serializable
    data class MediaRoles(
        val id: Int,
        val mediaRolesType: MediaRolesType,
        val roleTypes: List<RoleType>
    ): DetailsNavRoute

    @Serializable
    data class MediaReviews(
        val mediaId: Int,
        val mediaType: MediaType
    ): DetailsNavRoute

    @Serializable
    data class Review(val id: Int) : DetailsNavRoute
}