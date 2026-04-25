package com.example.shikiflow.presentation.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.repository.AuthRepository
import com.example.shikiflow.domain.repository.CacheRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.model.settings.ChapterUIMode
import com.example.shikiflow.domain.model.settings.AppUiMode
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.utils.ThemeMode
import com.materialkolor.PaletteStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val cacheRepository: CacheRepository
): ViewModel() {

    private val _settingsState = MutableStateFlow(SettingsUiState())
    val settingsState = _settingsState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.settingsFlow.distinctUntilChanged(),
                settingsRepository.themeSettingsFlow.distinctUntilChanged(),
                settingsRepository.mangaSettingsFlow.distinctUntilChanged(),
                settingsRepository.connectedServicesFlow.distinctUntilChanged()
            ) { settings, themeSettings, mangaSettings, connectedServices ->
                _settingsState.update { state ->
                    state.copy(
                        settings = settings,
                        themeSettings = themeSettings,
                        mangaSettings = mangaSettings,
                        connectedServices = connectedServices
                    )
                }
            }.collect()
        }

        viewModelScope.launch {
            settingsRepository.userFlow
                .filterNotNull()
                .first()
                .let { user ->
                    _settingsState.update { state ->
                        state.copy(user = user)
                    }
                }
        }

        viewModelScope.launch {
            settingsRepository.authTypeFlow
                .filterNotNull()
                .first()
                .let { authType ->
                    _settingsState.update { state ->
                        state.copy(authType = authType)
                    }
                }
        }
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
            val isSuccess = cacheRepository.clearCache()
            if (isSuccess) {
                loadCacheSize()
            }
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

    fun setDataSaver(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveDataSaverMode(isEnabled)
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