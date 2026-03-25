package com.example.shikiflow.presentation.screen.more.profile

import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.MainScreenNavOptions

interface ProfileNavOptions : MainScreenNavOptions {
    fun navigateToCompare(targetUser: User)
}