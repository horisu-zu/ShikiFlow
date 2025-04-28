package com.example.shikiflow.presentation.screen

interface MediaNavOptions: MainNavOptions {
    fun navigateToCharacterDetails(characterId: String)
    fun navigateToAnimeDetails(animeId: String)
    fun navigateToMangaDetails(mangaId: String)
}