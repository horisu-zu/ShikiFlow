package com.example.shikiflow.presentation.screen

import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import kotlinx.serialization.Serializable

sealed interface MainNavRoute : NavKey {
    @Serializable
    data object Home : MainNavRoute

    @Serializable
    data object Browse : MainNavRoute

    @Serializable
    data object More : MainNavRoute
}

sealed interface MainScreenNavRoute : NavKey {
    @Serializable
    data object MainTracks : MainScreenNavRoute

    @Serializable
    data class Details(val detailsNavRoute: DetailsNavRoute) : MainScreenNavRoute
}