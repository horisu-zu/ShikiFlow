package com.example.shikiflow.presentation.viewmodel.settings

import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.common.FileSize
import com.example.shikiflow.domain.model.settings.MangaChapterSettings
import com.example.shikiflow.domain.model.settings.Settings
import com.example.shikiflow.domain.model.settings.ThemeSettings
import com.example.shikiflow.domain.model.user.User

data class SettingsUiState(
    val user: User? = null,
    val authType: AuthType? = null,
    val connectedServices: Map<AuthType, User> = emptyMap(),
    val settings: Settings = Settings(),
    val themeSettings: ThemeSettings? = null,
    val mangaSettings: MangaChapterSettings = MangaChapterSettings(),
    val cacheSize: FileSize? = null
)
