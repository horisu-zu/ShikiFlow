package com.example.shikiflow.presentation.screen.more

import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.MainNavOptions

interface MoreNavOptions: MainNavOptions {
    fun navigateToProfile(user: User?)
    fun navigateToHistory()
    fun navigateToSettings()
    fun navigateToAbout()
}