package com.example.shikiflow.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppNavRoute : NavKey {
    @Serializable
    data object Splash : AppNavRoute

    @Serializable
    data object Auth : AppNavRoute

    @Serializable
    data object Main : AppNavRoute
}