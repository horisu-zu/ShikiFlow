package com.example.shikiflow.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.settings.MangaChapterSettings
import com.example.shikiflow.domain.model.settings.Settings
import com.example.shikiflow.domain.model.settings.ThemeSettings
import com.example.shikiflow.domain.repository.AuthRepository
import com.example.shikiflow.domain.repository.CacheRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.presentation.screen.main.MainTrackMode
import com.example.shikiflow.presentation.screen.main.details.manga.read.ChapterUIMode
import com.example.shikiflow.utils.AppUiMode
import com.example.shikiflow.utils.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val cacheRepository: CacheRepository
): ViewModel() {

    val settings = settingsRepository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = Settings()
        )

    val themeSettings = settingsRepository.themeSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ThemeSettings()
        )

    val mangaSettings = settingsRepository.mangaSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MangaChapterSettings()
        )

    private val _cacheSize = MutableStateFlow<String>("")
    val cacheSize = _cacheSize.asStateFlow()

    init {
        loadCacheSize()
    }

    fun loadCacheSize() {
        viewModelScope.launch {
            _cacheSize.value = cacheRepository.getCacheSize()
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

    fun setTrackMode(trackMode: MainTrackMode) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "setTrackMode: $trackMode")
            settingsRepository.saveTrackMode(trackMode)
        }
    }

    fun setAppUiMode(appUiMode: AppUiMode) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "setAppUiMode: $appUiMode")
            settingsRepository.saveAppUiMode(appUiMode)
        }
    }

    fun setTheme(themeMode: ThemeMode) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "setTheme: $themeMode")
            settingsRepository.saveTheme(themeMode)
        }
    }

    fun setOled(isEnabled: Boolean) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "setOled: $isEnabled")
            settingsRepository.saveOLEDMode(isEnabled)
        }
    }

    fun setDataSaver(isEnabled: Boolean) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "setDataSaver: $isEnabled")
            settingsRepository.saveDataSaverMode(isEnabled)
        }
    }

    fun setChapterUIMode(chapterUIMode: ChapterUIMode) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "setChapterUIMode: $chapterUIMode")
            settingsRepository.saveChapterUiMode(chapterUIMode)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}