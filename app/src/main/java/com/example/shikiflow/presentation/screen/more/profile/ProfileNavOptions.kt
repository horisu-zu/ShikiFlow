package com.example.shikiflow.presentation.screen.more.profile

import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.MainNavOptions

interface ProfileNavOptions : MainNavOptions {
    fun navigateToCompare(targetUser: User)
    fun navigateToSettings()
    fun navigateToAbout()
}