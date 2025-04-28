package com.example.shikiflow.presentation.screen.more

import kotlinx.serialization.Serializable

sealed interface MoreNavRoute {
    @Serializable
    object MoreScreen : MoreNavRoute

    @Serializable
    object ProfileScreen : MoreNavRoute

    @Serializable
    object HistoryScreen : MoreNavRoute

    /*@Serializable
    object SettingsScreen : MoreNavRoute

    @Serializable
    object ClubsScreen : MoreNavRoute*/
}