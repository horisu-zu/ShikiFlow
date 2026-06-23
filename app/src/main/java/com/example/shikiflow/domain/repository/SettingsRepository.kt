package com.example.shikiflow.domain.repository

import androidx.compose.ui.graphics.Color
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.settings.BrowseUiSettings
import com.example.shikiflow.domain.model.settings.MangaChapterSettings
import com.example.shikiflow.domain.model.settings.Settings
import com.example.shikiflow.domain.model.settings.ThemeSettings
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.settings.ChapterUIMode
import com.example.shikiflow.domain.model.settings.AppUiMode
import com.example.shikiflow.domain.model.settings.BrowseUiMode
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.UserSettings
import com.example.shikiflow.utils.ThemeMode
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val userFlow: Flow<User?>
    val userSettingsFlow: Flow<UserSettings?>
    val authTypeFlow: Flow<AuthType?>
    val connectedServicesFlow: Flow<Map<AuthType, User>>
    val settingsFlow: Flow<Settings>
    val themeSettingsFlow: Flow<ThemeSettings>
    val browseUiSettingsFlow: Flow<BrowseUiSettings>
    val mangaSettingsFlow: Flow<MangaChapterSettings>
    val localeFlow: Flow<String>
    val chapterLanguagesFlow: Flow<Set<String>>

    fun scoreFormat(authType: AuthType): Flow<ScoreFormat>

    suspend fun saveAuthType(authType: AuthType)
    suspend fun saveUserData(user: User, authType: AuthType)
    suspend fun saveServiceUpdatePreference(shouldUpdate: Boolean)
    suspend fun saveAppUiMode(appUiMode: AppUiMode)
    suspend fun saveBrowseUiMode(browseUiMode: BrowseUiMode)
    suspend fun saveBrowseOngoingOrder(ongoingOrder: MediaSort)
    suspend fun saveTheme(themeMode: ThemeMode)
    suspend fun saveOLEDMode(isEnabled: Boolean)
    suspend fun saveDynamicMode(isDynamicTheme: Boolean)
    suspend fun savePaletteStyle(paletteStyle: PaletteStyle)
    suspend fun savePrimaryColorPreferences(color: Color, useSystemWallpaperColor: Boolean)
    suspend fun saveLocale(locale: String)
    suspend fun saveTrackMode(mediaType: MediaType)
    suspend fun saveDataSaverMode(newMode: Boolean)
    suspend fun saveTrackerChapterUpdate(isEnabled: Boolean)
    suspend fun saveChapterUiMode(newMode: ChapterUIMode)
    suspend fun updateMangaSettings(settings: MangaChapterSettings)
    suspend fun saveChapterLanguages(chapterLanguages: Set<String>)
    suspend fun savePreferredTitleType(preferredType: PreferredTitleType)
    suspend fun saveScoreFormat(scoreFormat: ScoreFormat)

    suspend fun clearUserData()
    suspend fun clearUserData(authType: AuthType)
}