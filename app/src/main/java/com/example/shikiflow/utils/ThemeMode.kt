package com.example.shikiflow.utils

import com.example.shikiflow.R

enum class ThemeMode {
    SYSTEM, LIGHT, DARK;

    val displayValue: Int
        get() = when(this) {
            SYSTEM -> R.string.theme_mode_system
            LIGHT -> R.string.theme_mode_light
            DARK -> R.string.theme_mode_dark
        }

    val icon: IconResource
        get() = when(this) {
            SYSTEM -> IconResource.Drawable(resId = R.drawable.ic_system_theme)
            LIGHT -> IconResource.Drawable(resId = R.drawable.ic_light_theme)
            DARK -> IconResource.Drawable(resId = R.drawable.ic_dark_theme)
        }

    companion object {
        fun fromString(value: String?) = entries.find { it.name == value } ?: SYSTEM

        fun ThemeMode.isDarkTheme(isSystemInDarkTheme: Boolean): Boolean {
            return when(this) {
                SYSTEM -> isSystemInDarkTheme
                LIGHT -> false
                DARK -> true
            }
        }
    }
}