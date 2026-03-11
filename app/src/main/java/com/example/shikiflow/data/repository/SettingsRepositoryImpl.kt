package com.example.shikiflow.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.settings.BrowseUiSettings
import com.example.shikiflow.domain.model.settings.MangaChapterSettings
import com.example.shikiflow.domain.model.settings.Settings
import com.example.shikiflow.domain.model.settings.ThemeSettings
import com.example.shikiflow.domain.model.sort.BrowseOrder
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.presentation.screen.main.MainTrackMode
import com.example.shikiflow.presentation.screen.main.details.manga.read.ChapterUIMode
import com.example.shikiflow.utils.AppUiMode
import com.example.shikiflow.utils.BrowseUiMode
import com.example.shikiflow.utils.ThemeMode
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): SettingsRepository {

    companion object {
        private val USER_AVATAR_URL = stringPreferencesKey("user_avatar_url")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NICKNAME = stringPreferencesKey("user_nickname")
        private val USER_BANNER_URL = stringPreferencesKey("user_banner")
        private val AUTH_TYPE = stringPreferencesKey("auth_type")

        private val APP_UI_MODE = stringPreferencesKey("app_ui_mode")
        private val BROWSE_UI_MODE = stringPreferencesKey("browse_ui_mode")
        private val AL_BROWSE_ONGOING_ORDER = stringPreferencesKey("al_browse_ongoing_order")
        private val SHIKI_BROWSE_ONGOING_ORDER = stringPreferencesKey("shiki_browse_ongoing_order")

        private val THEME_KEY = stringPreferencesKey("theme")
        private val OLED_KEY = booleanPreferencesKey("oled")
        private val DYNAMIC_THEME_KEY = booleanPreferencesKey("dynamic_mode")
        private val PALETTE_STYLE_KEY = stringPreferencesKey("palette_style")

        private val LOCALE_KEY = stringPreferencesKey("locale")
        private val TRACK_MODE = stringPreferencesKey("track_theme")
        private val DATA_SAVER_MODE = booleanPreferencesKey("data_saver")
        private val CHAPTER_UI_MODE = stringPreferencesKey("chapter_ui_mode")
    }

    override val userFlow: Flow<User?> = dataStore.data
        .map { preferences ->
            preferences[USER_ID]?.let { userId ->
                User(
                    id = userId,
                    nickname = preferences[USER_NICKNAME] ?: "",
                    avatarUrl = preferences[USER_AVATAR_URL] ?: "",
                    profileBannerUrl = preferences[USER_BANNER_URL]
                )
            }
    }

    override val authTypeFlow = dataStore.data
        .map { preferences ->
            preferences[AUTH_TYPE]?.let { authType ->
                AuthType.valueOf(authType)
            } ?: AuthType.SHIKIMORI
        }

    override val settingsFlow: Flow<Settings> = dataStore.data
        .map { preferences ->
            Settings(
                appUiMode = AppUiMode.fromString(preferences[APP_UI_MODE]),
                browseUiMode = BrowseUiMode.fromString(preferences[BROWSE_UI_MODE]),
                trackMode = preferences[TRACK_MODE]?.let { MainTrackMode.valueOf(it) }
                    ?: MainTrackMode.ANIME,
                )
    }

    override val themeSettingsFlow: Flow<ThemeSettings> = dataStore.data
        .map { preferences ->
            ThemeSettings(
                themeMode = ThemeMode.fromString(preferences[THEME_KEY]),
                isOledEnabled = preferences[OLED_KEY] ?: false,
                isDynamicThemeEnabled = preferences[DYNAMIC_THEME_KEY] ?: false,
                paletteStyle = PaletteStyle.valueOf(preferences[PALETTE_STYLE_KEY] ?: PaletteStyle.Expressive.name),
            )
        }

    override val browseUiSettingsFlow: Flow<BrowseUiSettings> = dataStore.data
        .map { preferences ->
            BrowseUiSettings(
                appUiMode = AppUiMode.fromString(preferences[APP_UI_MODE]),
                browseUiMode = BrowseUiMode.fromString(preferences[BROWSE_UI_MODE]),
                browseOngoingOrder = getBrowseOngoingOrder(preferences)
            )
        }

    private suspend fun getBrowseOngoingOrder(preferences: Preferences): BrowseOrder {
        return when(authTypeFlow.first()) {
            AuthType.SHIKIMORI -> BrowseOrder.Shikimori.valueOf(
                value = preferences[SHIKI_BROWSE_ONGOING_ORDER] ?: BrowseOrder.Shikimori.RANKED_MAL.name
            )
            AuthType.ANILIST -> BrowseOrder.Anilist.valueOf(
                value = preferences[AL_BROWSE_ONGOING_ORDER] ?: BrowseOrder.Anilist.POPULARITY.name
            )
        }
    }

    override val mangaSettingsFlow: Flow<MangaChapterSettings> = dataStore.data
        .map { preferences ->
            MangaChapterSettings(
                chapterUIMode = ChapterUIMode.entries.find { it.name == preferences[CHAPTER_UI_MODE] }
                    ?: ChapterUIMode.SCROLL,
                isDataSaverEnabled = preferences[DATA_SAVER_MODE] ?: false
            )
        }

    override val localeFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[LOCALE_KEY] ?: Locale.getDefault().language
        }

    override suspend fun saveAuthType(authType: AuthType) {
        dataStore.edit { preferences ->
            preferences[AUTH_TYPE] = authType.name
        }
    }

    override suspend fun saveUserData(user: User) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = user.id
            preferences[USER_NICKNAME] = user.nickname
            preferences[USER_AVATAR_URL] = user.avatarUrl
            user.profileBannerUrl?.let { profileBannerUrl ->
                preferences[USER_BANNER_URL] = profileBannerUrl
            }
        }
    }

    override suspend fun saveAppUiMode(appUiMode: AppUiMode) {
        dataStore.edit { preferences ->
            preferences[APP_UI_MODE] = appUiMode.name
        }
    }

    override suspend fun saveBrowseUiMode(browseUiMode: BrowseUiMode) {
        dataStore.edit { preferences ->
            preferences[BROWSE_UI_MODE] = browseUiMode.name
        }
    }

    override suspend fun saveBrowseOngoingOrder(ongoingOrder: BrowseOrder) {
        dataStore.edit { preferences ->
            when (ongoingOrder) {
                is BrowseOrder.Shikimori -> preferences[SHIKI_BROWSE_ONGOING_ORDER] = ongoingOrder.name
                is BrowseOrder.Anilist -> preferences[AL_BROWSE_ONGOING_ORDER] = ongoingOrder.name
            }
        }
    }

    override suspend fun saveTheme(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeMode.name
        }
    }

    override suspend fun saveOLEDMode(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[OLED_KEY] = isEnabled
        }
    }

    override suspend fun saveDynamicMode(isDynamicTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[DYNAMIC_THEME_KEY] = isDynamicTheme
        }
    }

    override suspend fun savePaletteStyle(paletteStyle: PaletteStyle) {
        dataStore.edit { preferences ->
            preferences[PALETTE_STYLE_KEY] = paletteStyle.name
        }
    }

    override suspend fun saveLocale(locale: String) {
        dataStore.edit { preferences ->
            preferences[LOCALE_KEY] = locale
        }
    }

    override suspend fun saveTrackMode(trackMode: MainTrackMode) {
        dataStore.edit { preferences ->
            preferences[TRACK_MODE] = trackMode.name
        }
    }

    override suspend fun saveDataSaverMode(newMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[DATA_SAVER_MODE] = newMode
        }
    }

    override suspend fun saveChapterUiMode(newMode: ChapterUIMode) {
        dataStore.edit { preferences ->
            preferences[CHAPTER_UI_MODE] = newMode.name
        }
    }

    override suspend fun updateMangaSettings(settings: MangaChapterSettings) {
        dataStore.edit { preferences ->
            preferences[CHAPTER_UI_MODE] = settings.chapterUIMode.name
            preferences[DATA_SAVER_MODE] = settings.isDataSaverEnabled
        }
    }

    override suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(USER_NICKNAME)
            preferences.remove(USER_AVATAR_URL)
            preferences.remove(USER_BANNER_URL)
        }
    }
}