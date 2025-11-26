package com.example.shikiflow.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.presentation.screen.main.details.anime.watch.player.PlayerNavigate
import kotlinx.serialization.Serializable

sealed interface AppNavRoute : NavKey {
    @Serializable
    data object Splash : AppNavRoute

    @Serializable
    data object Auth : AppNavRoute

    @Serializable
    data object Main : AppNavRoute

    @Serializable
    data class Player(val playerNavigate: PlayerNavigate) : AppNavRoute
}