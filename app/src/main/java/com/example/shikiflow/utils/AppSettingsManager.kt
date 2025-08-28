package com.example.shikiflow.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.shikiflow.presentation.screen.main.MainTrackMode
import com.example.shikiflow.presentation.screen.main.details.manga.read.ChapterUIMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("app_settings")
        private val AVATAR_URL = stringPreferencesKey("avatar_url")
        private val USERNAME = stringPreferencesKey("username")
        private val USER_ID = intPreferencesKey("user_id")

        private val APP_UI_MODE = stringPreferencesKey("app_ui_mode")
        private val BROWSE_UI_MODE = stringPreferencesKey("browse_ui_mode")
        private val THEME_KEY = stringPreferencesKey("theme")
        private val OLED_KEY = stringPreferencesKey("oled")
        private val LOCALE_KEY = stringPreferencesKey("locale")
        private val TRACK_MODE = stringPreferencesKey("track_theme")
        private val DATA_SAVER_MODE = booleanPreferencesKey("data_saver")
        private val CHAPTER_UI_MODE = stringPreferencesKey("chapter_ui_mode")
    }

    val avatarUrlFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[AVATAR_URL] }

    val usernameFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USERNAME] }

    val userIdFlow: Flow<Int?> = context.dataStore.data
        .map { preferences -> preferences[USER_ID] }

    val appUiModeFlow: Flow<AppUiMode> = context.dataStore.data
        .map { preferences ->
            AppUiMode.fromString(preferences[APP_UI_MODE])
        }

    val browseUiModeFlow: Flow<BrowseUiMode> = context.dataStore.data
        .map { preferences ->
            BrowseUiMode.fromString(preferences[BROWSE_UI_MODE])
        }

    val themeFlow: Flow<ThemeMode> = context.dataStore.data
        .map { preferences ->
            ThemeMode.fromString(preferences[THEME_KEY])
        }

    val oledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[OLED_KEY]?.toBoolean() ?: false
        }

    val localeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LOCALE_KEY] ?: Locale.getDefault().language
        }

    val trackModeFlow: Flow<MainTrackMode> = context.dataStore.data
        .map { preferences ->
            preferences[TRACK_MODE]?.let { MainTrackMode.valueOf(it) } ?: MainTrackMode.ANIME
        }

    val dataSaverFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DATA_SAVER_MODE] ?: false
        }

    val chapterUiFlow: Flow<ChapterUIMode> = context.dataStore.data
        .map { preferences ->
            ChapterUIMode.entries.find { it.name == preferences[CHAPTER_UI_MODE] }
                ?: ChapterUIMode.SCROLL
        }

    suspend fun saveUserInfo(userId: Int, username: String, avatarUrl: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USERNAME] = username
            preferences[AVATAR_URL] = avatarUrl
        }
    }

    suspend fun clearUserInfo() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(USERNAME)
            preferences.remove(AVATAR_URL)
        }
    }

    suspend fun saveAppUiMode(appUiMode: AppUiMode) {
        context.dataStore.edit { preferences ->
            preferences[APP_UI_MODE] = appUiMode.name
        }
    }

    suspend fun saveBrowseUiMode(browseUiMode: BrowseUiMode) {
        context.dataStore.edit { preferences ->
            preferences[BROWSE_UI_MODE] = browseUiMode.name
        }
    }

    suspend fun saveTheme(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeMode.name
        }
    }

    suspend fun saveOLEDMode(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[OLED_KEY] = isEnabled.toString()
        }
    }

    suspend fun saveLocale(locale: String) {
        context.dataStore.edit { preferences ->
            preferences[LOCALE_KEY] = locale
        }
    }

    suspend fun saveTrackMode(trackMode: MainTrackMode) {
        context.dataStore.edit { preferences ->
            preferences[TRACK_MODE] = trackMode.name
        }
    }

    suspend fun saveDataSaverMode(newMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DATA_SAVER_MODE] = newMode
        }
    }

    suspend fun saveChapterUiMode(newMode: ChapterUIMode) {
        context.dataStore.edit { preferences ->
            preferences[CHAPTER_UI_MODE] = newMode.name
        }
    }
}