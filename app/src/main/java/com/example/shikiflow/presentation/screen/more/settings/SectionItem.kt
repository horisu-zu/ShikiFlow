package com.example.shikiflow.presentation.screen.more.settings

import com.example.shikiflow.utils.IconResource

sealed interface SectionItem {
    val title: String
    val displayValue: String

    data class Default(
        override val title: String,
        override val displayValue: String,
        val onClick: () -> Unit,
        val isVisible: Boolean = true
    ): SectionItem

    data class Switch(
        override val title: String,
        override val displayValue: String,
        val onClick: () -> Unit,
        val isChecked: Boolean,
        val isVisible: Boolean = true
    ): SectionItem

    data class Image(
        override val title: String,
        override val displayValue: String,
        val onClick: () -> Unit,
        val imageUrl: String
    ): SectionItem

    /*data class Theme(
        override val title: String,
        val onClick: (ThemeMode) -> Unit,
        val themeMode: ThemeMode
    ): SectionItem {
        override val displayValue: String
            get() = themeMode.name.lowercase().replaceFirstChar { it.uppercase() }
    }*/

    data class Mode(
        override val title: String,
        val entries: List<String>,
        val iconResources: List<IconResource> = emptyList(),
        val weights: List<Float> = emptyList(),
        val onClick: (String) -> Unit,
        val mode: String,
        val isVisible: Boolean = true
    ): SectionItem {
        override val displayValue: String
            get() = mode.lowercase().replaceFirstChar { it.uppercase() }
    }
}