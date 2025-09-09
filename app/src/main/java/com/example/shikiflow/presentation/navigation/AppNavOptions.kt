package com.example.shikiflow.presentation.navigation

import com.example.shikiflow.presentation.screen.MainNavOptions

interface AppNavOptions : MainNavOptions {
    fun navigateToAuth()
    fun navigateToMain()
    fun navigateToPlayer(
        title: String,
        link: String,
        translationGroup: String,
        serialNum: Int,
        offset: Int = 0,
        episodesCount: Int
    )
}