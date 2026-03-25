package com.example.shikiflow.presentation.screen.more.profile

import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.domain.model.user.User
import kotlinx.serialization.Serializable

sealed interface ProfileNavRoute : NavKey {
    @Serializable
    data class Profile(val user: User?) : ProfileNavRoute

    @Serializable
    data class MediaComparison(val targetUser: User) : ProfileNavRoute
}