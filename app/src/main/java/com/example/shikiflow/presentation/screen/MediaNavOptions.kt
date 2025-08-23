package com.example.shikiflow.presentation.screen

import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.presentation.screen.main.details.common.CommentsScreenMode

interface MediaNavOptions: MainNavOptions {
    fun navigateToCharacterDetails(characterId: String)
    fun navigateToAnimeDetails(animeId: String)
    fun navigateToMangaDetails(mangaId: String)
    fun navigateToSimilarPage(id: String, title: String, mediaType: MediaType)
    fun navigateToLinksPage(id: String, mediaType: MediaType)
    fun navigateToMangaRead(mangaDexIds: List<String>, title: String, completedChapters: Int)
    fun navigateToComments(screenMode: CommentsScreenMode, id: String)
}