package com.example.shikiflow.presentation.screen.more

import com.example.shikiflow.presentation.screen.MainScreenNavOptions

interface MoreNavOptions : MainScreenNavOptions {
    fun navigateToHistory()
    fun navigateToSettings()
    fun navigateToAbout()
}