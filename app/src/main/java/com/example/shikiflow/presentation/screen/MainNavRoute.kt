package com.example.shikiflow.presentation.screen

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface MainNavRoute : NavKey {
    @Serializable
    data object Home : MainNavRoute

    @Serializable
    data object Browse : MainNavRoute

    @Serializable
    data object More : MainNavRoute

    @Serializable
    data class AnimeDetails(val id: String) : MainNavRoute

    @Serializable
    data class MangaDetails(val id: String) : MainNavRoute

    @Serializable
    data class CharacterDetails(val characterId: String) : MainNavRoute
}