package com.example.shikiflow.domain.model.settings

import com.example.shikiflow.utils.ThemeMode

data class ThemeSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isOledEnabled: Boolean = false,
    //val isDynamicColorEnabled: Boolean = false
)
