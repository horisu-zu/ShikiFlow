package com.example.shikiflow.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.settings.BrowseUiSettings
import com.example.shikiflow.domain.model.settings.MangaChapterSettings
import com.example.shikiflow.domain.model.settings.Settings
import com.example.shikiflow.domain.model.settings.ThemeSettings
import com.example.shikiflow.domain.model.track.BrowseOrder
import com.example.shikiflow.domain.model.track.OrderOption
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.presentation.screen.main.MainTrackMode
import com.example.shikiflow.presentation.screen.main.details.manga.read.ChapterUIMode
import com.example.shikiflow.utils.AppUiMode
import com.example.shikiflow.utils.BrowseUiMode
import com.example.shikiflow.utils.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Locale
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val context: Context,
    private val scope: CoroutineScope
): SettingsRepository {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("app_settings")

    companion object {
        private val USER_AVATAR_URL = stringPreferencesKey("user_avatar_url")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NICKNAME = stringPreferencesKey("user_nickname")
        private val AUTH_TYPE = stringPreferencesKey("auth_type")

        private val APP_UI_MODE = stringPreferencesKey("app_ui_mode")
        private val BROWSE_UI_MODE = stringPreferencesKey("browse_ui_mode")
        private val AL_BROWSE_ONGOING_ORDER = stringPreferencesKey("al_browse_ongoing_order")
        private val SHIKI_BROWSE_ONGOING_ORDER = stringPreferencesKey("shiki_browse_ongoing_order")

        private val THEME_KEY = stringPreferencesKey("theme")
        private val OLED_KEY = stringPreferencesKey("oled")
        private val LOCALE_KEY = stringPreferencesKey("locale")
        private val TRACK_MODE = stringPreferencesKey("track_theme")
        private val DATA_SAVER_MODE = booleanPreferencesKey("data_saver")
        private val CHAPTER_UI_MODE = stringPreferencesKey("chapter_ui_mode")
    }

    override val userFlow: Flow<User?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ID]?.let { userId ->
                User(
                    id = userId,
                    nickname = preferences[USER_NICKNAME] ?: "",
                    avatarUrl = preferences[USER_AVATAR_URL] ?: ""
                )
            }
    }

    override val authTypeFlow: StateFlow<AuthType> = context.dataStore.data
        .map { preferences ->
            preferences[AUTH_TYPE]?.let { authType ->
                AuthType.valueOf(authType)
            } ?: AuthType.SHIKIMORI
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = AuthType.SHIKIMORI
        )

    override val settingsFlow: Flow<Settings> = context.dataStore.data
        .map { preferences ->
            Settings(
                appUiMode = AppUiMode.fromString(preferences[APP_UI_MODE]),
                browseUiMode = BrowseUiMode.fromString(preferences[BROWSE_UI_MODE]),
                trackMode = preferences[TRACK_MODE]?.let { MainTrackMode.valueOf(it) }
                    ?: MainTrackMode.ANIME,
                )
    }

    override val themeSettingsFlow: Flow<ThemeSettings> = context.dataStore.data
        .map { preferences ->
            ThemeSettings(
                themeMode = ThemeMode.fromString(preferences[THEME_KEY]),
                isOledEnabled = preferences[OLED_KEY]?.toBoolean() ?: false
            )
        }

    override val browseUiSettingsFlow: Flow<BrowseUiSettings> = context.dataStore.data
        .map { preferences ->
            BrowseUiSettings(
                appUiMode = AppUiMode.fromString(preferences[APP_UI_MODE]),
                browseUiMode = BrowseUiMode.fromString(preferences[BROWSE_UI_MODE]),
                browseOngoingOrder = getBrowseOngoingOrder(preferences)
            )
        }

    private suspend fun getBrowseOngoingOrder(preferences: Preferences): OrderOption {
        return when(authTypeFlow.first()) {
            AuthType.SHIKIMORI -> BrowseOrder.Shikimori.valueOf(
                value = preferences[SHIKI_BROWSE_ONGOING_ORDER] ?: BrowseOrder.Shikimori.RANKED_MAL.name
            )
            AuthType.ANILIST -> BrowseOrder.Anilist.valueOf(
                value = preferences[AL_BROWSE_ONGOING_ORDER] ?: BrowseOrder.Anilist.POPULARITY.name
            )
        }
    }

    override val mangaSettingsFlow: Flow<MangaChapterSettings> = context.dataStore.data
        .map { preferences ->
            MangaChapterSettings(
                chapterUIMode = ChapterUIMode.entries.find { it.name == preferences[CHAPTER_UI_MODE] }
                    ?: ChapterUIMode.SCROLL,
                isDataSaverEnabled = preferences[DATA_SAVER_MODE] ?: false
            )
        }

    override val localeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LOCALE_KEY] ?: Locale.getDefault().language
        }

    override suspend fun saveAuthType(authType: AuthType) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TYPE] = authType.name
        }
    }

    override suspend fun saveUserData(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = user.id
            preferences[USER_NICKNAME] = user.nickname
            preferences[USER_AVATAR_URL] = user.avatarUrl
        }
    }

    override suspend fun saveAppUiMode(appUiMode: AppUiMode) {
        context.dataStore.edit { preferences ->
            preferences[APP_UI_MODE] = appUiMode.name
        }
    }

    override suspend fun saveBrowseUiMode(browseUiMode: BrowseUiMode) {
        context.dataStore.edit { preferences ->
            preferences[BROWSE_UI_MODE] = browseUiMode.name
        }
    }

    override suspend fun saveBrowseOngoingOrder(ongoingOrder: BrowseOrder) {
        context.dataStore.edit { preferences ->
            when (ongoingOrder) {
                is BrowseOrder.Shikimori -> preferences[SHIKI_BROWSE_ONGOING_ORDER] = ongoingOrder.name
                is BrowseOrder.Anilist -> preferences[AL_BROWSE_ONGOING_ORDER] = ongoingOrder.name
            }
        }
    }

    override suspend fun saveTheme(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeMode.name
        }
    }

    override suspend fun saveOLEDMode(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[OLED_KEY] = isEnabled.toString()
        }
    }

    override suspend fun saveLocale(locale: String) {
        context.dataStore.edit { preferences ->
            preferences[LOCALE_KEY] = locale
        }
    }

    override suspend fun saveTrackMode(trackMode: MainTrackMode) {
        context.dataStore.edit { preferences ->
            preferences[TRACK_MODE] = trackMode.name
        }
    }

    override suspend fun saveDataSaverMode(newMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DATA_SAVER_MODE] = newMode
        }
    }

    override suspend fun saveChapterUiMode(newMode: ChapterUIMode) {
        context.dataStore.edit { preferences ->
            preferences[CHAPTER_UI_MODE] = newMode.name
        }
    }

    override suspend fun updateMangaSettings(settings: MangaChapterSettings) {
        context.dataStore.edit { preferences ->
            preferences[CHAPTER_UI_MODE] = settings.chapterUIMode.name
            preferences[DATA_SAVER_MODE] = settings.isDataSaverEnabled
        }
    }

    override suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(USER_NICKNAME)
            preferences.remove(USER_AVATAR_URL)
        }
    }
}