package com.example.shikiflow.presentation

import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.HEIGHT_DP_MEDIUM_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

enum class WindowSize {
    COMPACT,
    MEDIUM,
    EXPANDED;

    companion object {
        fun from(windowSizeClass: WindowSizeClass): WindowSize = when {
            windowSizeClass.isWidthAtLeastBreakpoint(
                widthDpBreakpoint = WIDTH_DP_EXPANDED_LOWER_BOUND
            ) && windowSizeClass.isHeightAtLeastBreakpoint(
                heightDpBreakpoint = HEIGHT_DP_MEDIUM_LOWER_BOUND
            ) -> EXPANDED
            windowSizeClass.isWidthAtLeastBreakpoint(
                widthDpBreakpoint = WIDTH_DP_MEDIUM_LOWER_BOUND
            ) -> MEDIUM
            else -> COMPACT
        }
    }
}