package com.example.shikiflow.domain.model.settings

import com.example.shikiflow.utils.ThemeMode
import com.materialkolor.PaletteStyle

data class ThemeSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isOledEnabled: Boolean = false,
    val isDynamicThemeEnabled: Boolean = false,
    val paletteStyle: PaletteStyle = PaletteStyle.Expressive
)
