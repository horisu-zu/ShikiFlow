package com.example.shikiflow.presentation.screen.more

import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserFavorite
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import kotlinx.serialization.Serializable

sealed interface MoreNavRoute : NavKey {
    @Serializable
    data object MoreScreen : MoreNavRoute

    @Serializable
    data class ProfileScreen(val user: User?) : MoreNavRoute

    @Serializable
    data object HistoryScreen : MoreNavRoute

    @Serializable
    data object SettingsScreen : MoreNavRoute

    @Serializable
    data object AboutAppScreen : MoreNavRoute

    @Serializable
    data class CompareScreen(val targetUser: User) : MoreNavRoute

    @Serializable
    data class Details(val detailsNavRoute: DetailsNavRoute) : MoreNavRoute
}