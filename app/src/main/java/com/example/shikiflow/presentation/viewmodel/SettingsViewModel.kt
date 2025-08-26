package com.example.shikiflow.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.repository.AuthRepository
import com.example.shikiflow.presentation.screen.main.details.manga.read.ChapterUIMode
import com.example.shikiflow.utils.AppSettingsManager
import com.example.shikiflow.utils.CacheManager
import com.example.shikiflow.utils.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsManager: AppSettingsManager,
    private val authRepository: AuthRepository,
    private val cacheManager: CacheManager
): ViewModel() {
    private val _appTheme = MutableStateFlow<ThemeMode>(ThemeMode.SYSTEM)
    val appTheme = _appTheme.asStateFlow()

    private val _isOledThemeEnabled = MutableStateFlow<Boolean>(false)
    val isOLEDModeEnabled = _isOledThemeEnabled.asStateFlow()

    private val _isDataSaver = MutableStateFlow<Boolean>(false)
    val isDataSaver = _isDataSaver.asStateFlow()

    private val _chapterUIMode = MutableStateFlow<ChapterUIMode>(ChapterUIMode.SCROLL)
    val chapterUIMode = _chapterUIMode.asStateFlow()

    private val _cacheSize = MutableStateFlow<String>("")
    val cacheSize = _cacheSize.asStateFlow()

    init {
        loadAppTheme()
        loadCacheSize()
        loadMangaSettings()
    }

    private fun loadAppTheme() {
        viewModelScope.launch {
            combine(
                appSettingsManager.themeFlow.distinctUntilChanged(),
                appSettingsManager.oledFlow.distinctUntilChanged()
            ) { appTheme, isOledEnabled ->
                _appTheme.value = appTheme
                _isOledThemeEnabled.value = isOledEnabled
            }.collect()
        }
    }

    private fun loadMangaSettings() {
        viewModelScope.launch {
            combine(
                appSettingsManager.dataSaverFlow.distinctUntilChanged(),
                appSettingsManager.chapterUiFlow.distinctUntilChanged()
            ) { isDataSaverEnabled, chapterUIMode ->
                _isDataSaver.value = isDataSaverEnabled
                _chapterUIMode.value = chapterUIMode
            }.collect()
        }
    }

    fun loadCacheSize() {
        viewModelScope.launch {
            _cacheSize.value = cacheManager.getCacheSize()
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            val isSuccess = cacheManager.clearCache()
            if (isSuccess) {
                loadCacheSize()
            }
        }
    }

    fun setTheme(themeMode: ThemeMode) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "setTheme: $themeMode")
            appSettingsManager.saveTheme(themeMode)
        }
    }

    fun setOled(isEnabled: Boolean) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "setOled: $isEnabled")
            appSettingsManager.saveOLEDMode(isEnabled)
        }
    }

    fun setDataSaver(isEnabled: Boolean) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "setDataSaver: $isEnabled")
            appSettingsManager.saveDataSaverMode(isEnabled)
        }
    }

    fun setChapterUIMode(chapterUIMode: ChapterUIMode) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "setChapterUIMode: $chapterUIMode")
            appSettingsManager.saveChapterUiMode(chapterUIMode)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}