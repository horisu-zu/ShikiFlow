package com.example.shikiflow.presentation.screen.more

import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import kotlinx.serialization.Serializable

sealed interface MoreNavRoute : NavKey {
    @Serializable
    data object MoreMain : MoreNavRoute

    @Serializable
    data class Profile(val user: User?) : MoreNavRoute

    @Serializable
    data object UserActivity : MoreNavRoute

    @Serializable
    data object Settings : MoreNavRoute

    @Serializable
    data object AboutApp : MoreNavRoute

    @Serializable
    data class Details(val detailsNavRoute: DetailsNavRoute) : MoreNavRoute
}