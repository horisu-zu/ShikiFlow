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
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.model.settings.ChapterUIMode
import com.example.shikiflow.domain.model.settings.AppUiMode
import com.example.shikiflow.domain.model.settings.BrowseUiMode
import com.example.shikiflow.domain.model.tracks.MediaType
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
        private val AUTH_TYPE = stringPreferencesKey("auth_type")

        private fun userId(authType: AuthType) = stringPreferencesKey("${authType}user_id")
        private fun userAvatar(authType: AuthType) = stringPreferencesKey("${authType}_user_avatar_url")
        private fun userNickname(authType: AuthType) = stringPreferencesKey("${authType}user_nickname")
        private fun userBanner(authType: AuthType) = stringPreferencesKey("${authType}user_banner")

        private val TRACKER_SERVICE_UPDATE = booleanPreferencesKey("tracker_update")
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
            val currentAuthType = preferences[AUTH_TYPE]?.let { authType ->
                AuthType.valueOf(authType)
            }

            currentAuthType?.let { authType ->
                preferences[userId(authType)]?.let { userId ->
                    User(
                        id = userId.toInt(),
                        nickname = preferences[userNickname(authType)] ?: "",
                        avatarUrl = preferences[userAvatar(authType)] ?: "",
                        profileBannerUrl = preferences[userBanner(authType)]
                    )
                }
            }
    }

    override val authTypeFlow = dataStore.data
        .map { preferences ->
            preferences[AUTH_TYPE]?.let { authType ->
                AuthType.valueOf(authType)
            }
        }

    override val connectedServicesFlow: Flow<Map<AuthType, User>> = dataStore.data
        .map { preferences ->
            val currentAuthType = preferences[AUTH_TYPE]?.let { authType ->
                AuthType.valueOf(authType)
            }

            AuthType.entries
                .filter { it != currentAuthType }
                .mapNotNull { authType ->
                    preferences[userId(authType)]?.let { userId ->
                        authType to User(
                            id = userId.toInt(),
                            nickname = preferences[userNickname(authType)] ?: "",
                            avatarUrl = preferences[userAvatar(authType)] ?: "",
                            profileBannerUrl = preferences[userBanner(authType)]
                        )
                    }
                }
                .toMap()
        }

    override val settingsFlow: Flow<Settings> = dataStore.data
        .map { preferences ->
            Settings(
                serviceUpdateState = preferences[TRACKER_SERVICE_UPDATE] ?: true,
                appUiMode = AppUiMode.fromString(preferences[APP_UI_MODE]),
                browseUiMode = BrowseUiMode.fromString(preferences[BROWSE_UI_MODE]),
                trackMode = preferences[TRACK_MODE]?.let { MediaType.valueOf(it) }
                    ?: MediaType.ANIME,
                )
    }

    override val themeSettingsFlow: Flow<ThemeSettings> = dataStore.data
        .map { preferences ->
            ThemeSettings(
                themeMode = ThemeMode.valueOf(preferences[THEME_KEY] ?: ThemeMode.SYSTEM.name),
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

    private suspend fun getBrowseOngoingOrder(preferences: Preferences): MediaSort {
        return when(authTypeFlow.first()) {
            AuthType.SHIKIMORI -> MediaSort.Shikimori.from(
                name = preferences[SHIKI_BROWSE_ONGOING_ORDER] ?: MediaSort.Common.SCORE.name
            )
            AuthType.ANILIST -> MediaSort.Anilist.from(
                name = preferences[AL_BROWSE_ONGOING_ORDER] ?: MediaSort.Common.POPULARITY.name
            )
            else -> MediaSort.Common.POPULARITY
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

    override suspend fun saveUserData(user: User, authType: AuthType) {
        dataStore.edit { preferences ->
            preferences[userId(authType)] = user.id.toString()
            preferences[userNickname(authType)] = user.nickname
            preferences[userAvatar(authType)] = user.avatarUrl
            user.profileBannerUrl?.let { profileBannerUrl ->
                preferences[userBanner(authType)] = profileBannerUrl
            }
        }
    }

    override suspend fun saveServiceUpdatePreference(shouldUpdate: Boolean) {
        dataStore.edit { preferences ->
            preferences[TRACKER_SERVICE_UPDATE] = shouldUpdate
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

    override suspend fun saveBrowseOngoingOrder(ongoingOrder: MediaSort) {
        val key = when(authTypeFlow.first()) {
            AuthType.SHIKIMORI -> SHIKI_BROWSE_ONGOING_ORDER
            AuthType.ANILIST -> AL_BROWSE_ONGOING_ORDER
            else -> SHIKI_BROWSE_ONGOING_ORDER
        }

        dataStore.edit { preferences ->
            preferences[key] = ongoingOrder.name
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

    override suspend fun saveTrackMode(mediaType: MediaType) {
        dataStore.edit { preferences ->
            preferences[TRACK_MODE] = mediaType.name
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
            AuthType.entries.forEach { authType ->
                preferences.remove(userId(authType))
                preferences.remove(userNickname(authType))
                preferences.remove(userAvatar(authType))
                preferences.remove(userBanner(authType))
            }

            preferences.remove(AUTH_TYPE)
        }
    }

    override suspend fun clearUserData(authType: AuthType) {
        dataStore.edit { preferences ->
            preferences.remove(userId(authType))
            preferences.remove(userNickname(authType))
            preferences.remove(userAvatar(authType))
            preferences.remove(userBanner(authType))
        }
    }
}