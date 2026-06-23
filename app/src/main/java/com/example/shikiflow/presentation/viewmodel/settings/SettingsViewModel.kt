package com.example.shikiflow.presentation.viewmodel.settings

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.repository.AuthRepository
import com.example.shikiflow.domain.repository.CacheRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.model.settings.ChapterUIMode
import com.example.shikiflow.domain.model.settings.AppUiMode
import com.example.shikiflow.domain.model.settings.MangaChapterSettings
import com.example.shikiflow.domain.model.settings.Settings
import com.example.shikiflow.domain.model.settings.ThemeSettings
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserSettings
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.utils.DataResult
import com.example.shikiflow.utils.ThemeMode
import com.example.shikiflow.worker.MediaTracksScheduler
import com.materialkolor.PaletteStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val cacheRepository: CacheRepository,
    private val mediaTracksScheduler: MediaTracksScheduler
): ViewModel() {

    private val _settingsState = MutableStateFlow(SettingsUiState())
    val settingsState = _settingsState.asStateFlow()

    init {
        combine(
            settingsRepository.settingsFlow.distinctUntilChanged(),
            settingsRepository.themeSettingsFlow.distinctUntilChanged(),
            settingsRepository.mangaSettingsFlow.distinctUntilChanged(),
            settingsRepository.userSettingsFlow.distinctUntilChanged(),
            settingsRepository.connectedServicesFlow.distinctUntilChanged(),
            settingsRepository.chapterLanguagesFlow.distinctUntilChanged()
        ) { values ->
            _settingsState.update { state ->
                state.copy(
                    settings = values[0] as Settings,
                    themeSettings = values[1] as ThemeSettings,
                    mangaSettings = values[2] as MangaChapterSettings,
                    userSettings = values[3] as UserSettings,
                    connectedServices = values[4] as Map<AuthType, User>,
                    chapterLanguages = values[5] as Set<String>
                )
            }
        }.launchIn(viewModelScope)

        settingsRepository.userFlow
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { user ->
                _settingsState.update { state ->
                    state.copy(user = user)
                }
            }.launchIn(viewModelScope)

        settingsRepository.authTypeFlow
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { authType ->
                _settingsState.update { state ->
                    state.copy(authType = authType)
                }
            }.launchIn(viewModelScope)
    }

    fun getAuthorizationUrl(authType: AuthType): String {
        return authRepository.getAuthorizationUrl(authType)
    }

    fun loadCacheSize() {
        viewModelScope.launch {
            _settingsState.update { state ->
                state.copy(cacheSize = cacheRepository.getCacheSize())
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            cacheRepository.clearCache().let { isSuccess ->
                if(isSuccess) {
                    loadCacheSize()
                }
            }
        }
    }

    fun setAuthType(authType: AuthType, userId: Int) {
        viewModelScope.launch {
            settingsRepository.saveAuthType(authType)

            mediaTracksScheduler.scheduleOneTimeSync(userId)
        }
    }

    fun clearUserData(authType: AuthType) {
        viewModelScope.launch {
            settingsRepository.clearUserData(authType)
        }
    }

    fun setTrackerServiceUpdate(shouldUpdate: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveServiceUpdatePreference(shouldUpdate)
        }
    }

    fun setTrackMode(mediaType: MediaType) {
        viewModelScope.launch {
            settingsRepository.saveTrackMode(mediaType)
        }
    }

    fun setAppUiMode(appUiMode: AppUiMode) {
        viewModelScope.launch {
            settingsRepository.saveAppUiMode(appUiMode)
        }
    }

    fun setTitleType(preferredType: PreferredTitleType) {
        viewModelScope.launch {
            settingsRepository.savePreferredTitleType(preferredType)
        }
    }

    fun setScoreFormat(scoreFormat: ScoreFormat) {
        viewModelScope.launch {
            userRepository.setUserSettings(scoreFormat = scoreFormat)
                .also { result ->
                    if(result is DataResult.Success) {
                        settingsRepository.saveScoreFormat(result.data.scoreFormat)
                    }
                }
        }
    }

    fun setTheme(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.saveTheme(themeMode)
        }
    }

    fun setOled(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveOLEDMode(isEnabled)
        }
    }

    fun setDynamicTheme(isDynamicTheme: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveDynamicMode(isDynamicTheme)
        }
    }

    fun setPaletteStyle(paletteStyle: PaletteStyle) {
        viewModelScope.launch {
            settingsRepository.savePaletteStyle(paletteStyle)
        }
    }

    fun setPrimaryColorPreferences(color: Color, useSystemWallpaperColor: Boolean) {
        viewModelScope.launch {
            settingsRepository.savePrimaryColorPreferences(color, useSystemWallpaperColor)
        }
    }

    fun setDataSaver(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveDataSaverMode(isEnabled)
        }
    }

    fun setChapterLanguages(languagesSet: Set<String>) {
        viewModelScope.launch {
            settingsRepository.saveChapterLanguages(languagesSet)
        }
    }

    fun setTrackerChapterUpdate(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveTrackerChapterUpdate(isEnabled)
        }
    }

    fun setChapterUIMode(chapterUIMode: ChapterUIMode) {
        viewModelScope.launch {
            settingsRepository.saveChapterUiMode(chapterUIMode)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}