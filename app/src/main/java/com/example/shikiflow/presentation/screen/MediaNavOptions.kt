package com.example.shikiflow.presentation.screen

import com.example.shikiflow.data.tracks.MediaType

interface MediaNavOptions: MainNavOptions {
    fun navigateToCharacterDetails(characterId: String)
    fun navigateToAnimeDetails(animeId: String)
    fun navigateToMangaDetails(mangaId: String)
    fun navigateToSimilarPage(id: String, title: String, mediaType: MediaType)
    fun navigateToLinksPage(id: String, mediaType: MediaType)
    fun navigateToMangaRead(mangaDexId: String, title: String, completedChapters: Int)
}