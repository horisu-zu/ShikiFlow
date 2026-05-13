package com.example.shikiflow.presentation.screen

import androidx.compose.runtime.compositionLocalOf

class BottomBarState(val setVisibility: (Boolean) -> Unit)

val LocalBottomBarController = compositionLocalOf<BottomBarState> {
    error("BottomBarController not provided")
}