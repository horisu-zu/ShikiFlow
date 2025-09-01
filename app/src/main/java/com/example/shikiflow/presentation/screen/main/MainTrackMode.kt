package com.example.shikiflow.presentation.screen.main

import com.example.shikiflow.R

enum class MainTrackMode {
    ANIME,
    MANGA;

    val displayValue: Int
        get() = when(this) {
            ANIME -> R.string.main_track_mode_anime
            MANGA -> R.string.main_track_mode_manga
        }
}