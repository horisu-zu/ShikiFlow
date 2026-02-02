package com.example.shikiflow.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import com.example.shikiflow.R

enum class AppUiMode {
    LIST, GRID;

    companion object {
        fun fromString(value: String?) = entries.find { it.name == value } ?: LIST
    }

    val displayValue: Int
        get() = when(this) {
            LIST -> R.string.app_ui_mode_list
            GRID -> R.string.app_ui_mode_grid
        }
}

enum class BrowseUiMode {
    AUTO, LIST, GRID;

    companion object {
        fun fromString(value: String?) = entries.find { it.name == value } ?: AUTO
    }

    val displayValue: Int
        get() = when(this) {
            AUTO -> R.string.browse_ui_mode_auto
            LIST -> R.string.browse_ui_mode_list
            GRID -> R.string.browse_ui_mode_grid
        }

    val icon: IconResource
        get() = when(this) {
            AUTO -> IconResource.Drawable(resId = R.drawable.ic_stars)
            LIST -> IconResource.Vector(imageVector = Icons.AutoMirrored.Filled.List)
            GRID -> IconResource.Drawable(resId = R.drawable.ic_grid)
        }
}
