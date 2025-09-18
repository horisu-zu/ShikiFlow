package com.example.shikiflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.presentation.screen.main.MainTrackMode
import com.example.shikiflow.utils.AppSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appSettingsManager: AppSettingsManager
): ViewModel() {

    private val _currentTrackMode = MutableStateFlow<MainTrackMode?>(null)
    val currentTrackMode = _currentTrackMode.asStateFlow()

    init {
        viewModelScope.launch {
            appSettingsManager.trackModeFlow.collect { trackMode ->
                _currentTrackMode.value = trackMode
            }
        }
    }

    fun setCurrentTrackMode(mode: MainTrackMode) {
        viewModelScope.launch {
            _currentTrackMode.value = mode
        }
    }
}