package com.example.shikiflow.presentation.screen.main.details.manga.read

import com.example.shikiflow.R
import com.example.shikiflow.utils.IconResource

enum class ChapterUIMode {
    PAGE,
    SCROLL;

    val displayValue: Int
        get() = when(this) {
            PAGE -> R.string.chapter_ui_mode_page
            SCROLL -> R.string.chapter_ui_mode_scroll
        }

    val icon: IconResource
        get() = when(this) {
            PAGE -> IconResource.Drawable(resId = R.drawable.ic_manga)
            SCROLL -> IconResource.Drawable(resId = R.drawable.ic_scroll)
        }
}