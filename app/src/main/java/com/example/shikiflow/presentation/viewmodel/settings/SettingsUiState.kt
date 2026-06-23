package com.example.shikiflow.presentation.viewmodel.settings

import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.common.FileSize
import com.example.shikiflow.domain.model.settings.MangaChapterSettings
import com.example.shikiflow.domain.model.settings.Settings
import com.example.shikiflow.domain.model.settings.ThemeSettings
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserSettings

data class SettingsUiState(
    val user: User? = null,
    val authType: AuthType? = null,
    val userSettings: UserSettings = UserSettings(),
    val connectedServices: Map<AuthType, User> = emptyMap(),
    val settings: Settings = Settings(),
    val themeSettings: ThemeSettings? = null,
    val mangaSettings: MangaChapterSettings = MangaChapterSettings(),
    val chapterLanguages: Set<String> = emptySet(),
    val cacheSize: FileSize? = null
)
