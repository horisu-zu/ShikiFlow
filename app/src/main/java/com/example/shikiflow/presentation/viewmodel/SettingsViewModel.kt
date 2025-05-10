package com.example.shikiflow.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.utils.AppSettingsManager
import com.example.shikiflow.utils.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsManager: AppSettingsManager
): ViewModel() {
    private val _appTheme = MutableStateFlow<ThemeMode>(ThemeMode.SYSTEM)
    val appTheme = _appTheme.asStateFlow()

    init {
        loadAppTheme()
    }

    private fun loadAppTheme() {
        viewModelScope.launch {
            appSettingsManager.themeFlow.collect { appTheme ->
                _appTheme.value = appTheme
            }
        }
    }

    fun setTheme(themeMode: ThemeMode) {
        viewModelScope.launch {
            Log.d("SettingsViewModel", "setTheme: $themeMode")
            appSettingsManager.saveTheme(themeMode)
            _appTheme.value = themeMode
        }
    }
}