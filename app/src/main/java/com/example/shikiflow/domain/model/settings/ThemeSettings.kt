package com.example.shikiflow.domain.model.settings

import androidx.compose.ui.graphics.Color
import com.example.shikiflow.utils.ThemeMode
import com.materialkolor.PaletteStyle

data class ThemeSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isOledEnabled: Boolean = false,
    val isDynamicThemeEnabled: Boolean = false,
    val paletteStyle: PaletteStyle = PaletteStyle.Expressive,
    val primaryColor: Color = Color(0xFF526CFD),
    val useSystemWallpaperColor: Boolean = true
)
