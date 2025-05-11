package com.example.shikiflow.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.repository.AuthRepository
import com.example.shikiflow.utils.AppSettingsManager
import com.example.shikiflow.utils.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsManager: AppSettingsManager,
    private val authRepository: AuthRepository
): ViewModel() {
    private val _appTheme = MutableStateFlow<ThemeMode>(ThemeMode.SYSTEM)
    val appTheme = _appTheme.asStateFlow()

    private val _isOledThemeEnabled = MutableStateFlow<Boolean>(false)
    val isOLEDModeEnabled = _isOledThemeEnabled.asStateFlow()

    init {
        loadAppTheme()
    }

    private fun loadAppTheme() {
        viewModelScope.launch {
            combine(
                appSettingsManager.themeFlow,
                appSettingsManager.oledFlow
            ) { appTheme, isOledEnabled ->
                _appTheme.value = appTheme
                Log.d("SettingsViewModel", "OLED mode: $isOledEnabled")
                _isOledThemeEnabled.value = isOledEnabled
            }.collect { /**/ }
        }
    }

    fun setTheme(themeMode: ThemeMode) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "setTheme: $themeMode")
            appSettingsManager.saveTheme(themeMode)
            _appTheme.value = themeMode
        }
    }

    fun setOled(isEnabled: Boolean) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "setOled: $isEnabled")
            appSettingsManager.saveOLEDMode(isEnabled)
            _isOledThemeEnabled.value = isEnabled
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}