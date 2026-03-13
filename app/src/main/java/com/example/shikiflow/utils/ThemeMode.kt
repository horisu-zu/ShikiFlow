package com.example.shikiflow.utils

enum class ThemeMode {
    SYSTEM, LIGHT, DARK;

    companion object {
        fun ThemeMode.isDarkTheme(isSystemInDarkTheme: Boolean): Boolean {
            return when(this) {
                SYSTEM -> isSystemInDarkTheme
                LIGHT -> false
                DARK -> true
            }
        }
    }
}