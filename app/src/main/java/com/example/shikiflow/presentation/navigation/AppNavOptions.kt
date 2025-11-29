package com.example.shikiflow.presentation.navigation

import com.example.shikiflow.presentation.screen.MainNavOptions

interface AppNavOptions : MainNavOptions {
    fun navigateToAuth()
    fun navigateToMain()
}