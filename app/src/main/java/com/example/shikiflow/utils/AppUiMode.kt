package com.example.shikiflow.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List

enum class AppUiMode {
    LIST, GRID;

    companion object {
        fun fromString(value: String?) = entries.find { it.name == value } ?: LIST
    }

    val displayValue: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }
}

enum class BrowseUiMode {
    AUTO, LIST, GRID;

    companion object {
        fun fromString(value: String?) = entries.find { it.name == value } ?: AUTO
    }

    val displayValue: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }

    val icon: IconResource
        get() = when(this) {
            AUTO -> IconResource.Drawable(resId = com.example.shikiflow.R.drawable.ic_stars)
            LIST -> IconResource.Vector(imageVector = Icons.AutoMirrored.Filled.List)
            GRID -> IconResource.Drawable(resId = com.example.shikiflow.R.drawable.ic_grid)
        }
}