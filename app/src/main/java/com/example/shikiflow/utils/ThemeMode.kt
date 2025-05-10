package com.example.shikiflow.utils

import com.example.shikiflow.R

enum class ThemeMode {
    SYSTEM, LIGHT, DARK;

    companion object {
        fun fromString(value: String?) = entries.find { it.name == value } ?: SYSTEM
    }

    val displayValue: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }

    val icon: IconResource
        get() = when(this) {
            SYSTEM -> IconResource.Drawable(resId = R.drawable.ic_system_theme)
            LIGHT -> IconResource.Drawable(resId = R.drawable.ic_light_theme)
            DARK -> IconResource.Drawable(resId = R.drawable.ic_dark_theme)
        }
}