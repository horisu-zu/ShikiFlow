package com.example.shikiflow.presentation.screen.main.details

import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.NavOptions

interface MediaNavOptions: NavOptions {
    fun navigateToCharacterDetails(characterId: Int)

    fun navigateToMediaCharacters(mediaId: Int, mediaTitle: String, mediaType: MediaType)

    fun navigateToAnimeDetails(animeId: Int)

    fun navigateToMangaDetails(mangaId: Int)

    fun navigateToSimilarPage(id: Int, title: String, mediaType: MediaType)

    fun navigateToLinksPage(id: Int, mediaType: MediaType)

    fun navigateToMangaRead(mangaDexIds: List<String>, title: String, completedChapters: Int)

    fun navigateToThreads(mediaId: Int)

    fun navigateToComments(screenMode: CommentsScreenMode, id: Int, threadHeader: Thread? = null)

    fun navigateToAnimeWatch(title: String, shikimoriId: Int, completedEpisodes: Int)

    fun navigateByEntity(entityType: EntityType, id: Int)

    fun navigateToStaff(staffId: Int)

    fun navigateToMediaStaff(mediaId: Int, mediaType: MediaType)

    fun navigateToStudio(id: Int, studioName: String)

    fun navigateToMediaRoles(id: Int, mediaRolesType: MediaRolesType, roleTypes: List<RoleType>)

    fun navigateToUserProfile(user: User)
}