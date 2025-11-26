package com.example.shikiflow.presentation.navigation

import com.example.shikiflow.presentation.screen.MainNavOptions
import com.example.shikiflow.presentation.screen.main.details.anime.watch.player.PlayerNavigate

interface AppNavOptions : MainNavOptions {
    fun navigateToAuth()
    fun navigateToMain()
    fun navigateToPlayer(playerNavigate: PlayerNavigate)
}