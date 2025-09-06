package com.example.shikiflow.presentation.navigation

interface AppNavOptions {
    fun navigateToAuth()
    fun navigateToMain()
    fun navigateToPlayer(link: String, serialNum: Int)
}