package com.example.shikiflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.presentation.screen.main.MainTrackMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _currentTrackMode = MutableStateFlow<MainTrackMode?>(null)
    val currentTrackMode = _currentTrackMode.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.map { it.trackMode }.collect { trackMode ->
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