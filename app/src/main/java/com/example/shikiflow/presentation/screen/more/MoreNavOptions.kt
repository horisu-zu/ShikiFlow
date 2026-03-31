package com.example.shikiflow.presentation.screen.more

import com.example.shikiflow.presentation.screen.MainNavOptions

interface MoreNavOptions : MainNavOptions {
    fun navigateToHistory()
    fun navigateToSettings()
    fun navigateToAbout()
}