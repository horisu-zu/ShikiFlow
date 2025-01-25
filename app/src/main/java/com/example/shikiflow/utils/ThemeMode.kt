package com.example.shikiflow.utils

enum class ThemeMode {
    SYSTEM, LIGHT, DARK;

    companion object {
        fun fromString(value: String?) = entries.find { it.name == value } ?: SYSTEM
    }
}