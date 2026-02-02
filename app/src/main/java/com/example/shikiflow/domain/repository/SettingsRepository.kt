package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.settings.BrowseUiSettings
import com.example.shikiflow.domain.model.settings.MangaChapterSettings
import com.example.shikiflow.domain.model.settings.Settings
import com.example.shikiflow.domain.model.settings.ThemeSettings
import com.example.shikiflow.domain.model.track.BrowseOrder
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.main.MainTrackMode
import com.example.shikiflow.presentation.screen.main.details.manga.read.ChapterUIMode
import com.example.shikiflow.utils.AppUiMode
import com.example.shikiflow.utils.BrowseUiMode
import com.example.shikiflow.utils.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val userFlow: Flow<User?>
    val authTypeFlow: StateFlow<AuthType>
    val settingsFlow: Flow<Settings>
    val themeSettingsFlow: Flow<ThemeSettings>
    val browseUiSettingsFlow: Flow<BrowseUiSettings>
    val mangaSettingsFlow: Flow<MangaChapterSettings>
    val localeFlow: Flow<String>

    suspend fun saveAuthType(authType: AuthType)
    suspend fun saveUserData(user: User)
    suspend fun saveAppUiMode(appUiMode: AppUiMode)
    suspend fun saveBrowseUiMode(browseUiMode: BrowseUiMode)
    suspend fun saveBrowseOngoingOrder(ongoingOrder: BrowseOrder)
    suspend fun saveTheme(themeMode: ThemeMode)
    suspend fun saveOLEDMode(isEnabled: Boolean)
    suspend fun saveLocale(locale: String)
    suspend fun saveTrackMode(trackMode: MainTrackMode)
    suspend fun saveDataSaverMode(newMode: Boolean)
    suspend fun saveChapterUiMode(newMode: ChapterUIMode)
    suspend fun updateMangaSettings(settings: MangaChapterSettings)

    suspend fun clearUserData()
}