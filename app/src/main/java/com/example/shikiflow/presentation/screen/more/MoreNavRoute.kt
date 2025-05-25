package com.example.shikiflow.presentation.screen.more

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface MoreNavRoute : NavKey {
    @Serializable
    object MoreScreen : MoreNavRoute

    @Serializable
    object ProfileScreen : MoreNavRoute

    @Serializable
    object HistoryScreen : MoreNavRoute

    @Serializable
    object SettingsScreen : MoreNavRoute

    @Serializable
    object AboutAppScreen : MoreNavRoute
}