package com.example.shikiflow.presentation.screen

import androidx.compose.runtime.compositionLocalOf

class NavBarState(val setVisibility: (Boolean) -> Unit)

val LocalNavBarController = compositionLocalOf<NavBarState> {
    error("NavBarController not provided")
}