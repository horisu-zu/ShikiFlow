package com.example.shikiflow.presentation.screen.main.details.anime.watch

import com.example.shikiflow.R

enum class TranslationFilter {
    ALL,
    VOICE,
    SUBTITLE;

    val displayValue: Int
        get() = when(this) {
            ALL -> R.string.translation_filter_all
            VOICE -> R.string.translation_filter_voice
            SUBTITLE -> R.string.translation_filter_subtitles
        }
}