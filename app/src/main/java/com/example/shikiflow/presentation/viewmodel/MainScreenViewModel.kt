package com.example.shikiflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.model.tracks.MediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MainScreenViewModel @Inject constructor(
    settingsRepository: SettingsRepository
): ViewModel() {

    private val _currentTrackMode = MutableStateFlow<MediaType?>(null)
    val currentTrackMode = _currentTrackMode.asStateFlow()

    init {
        settingsRepository.settingsFlow
            .distinctUntilChangedBy { it.trackMode }
            .onEach { settings ->
                _currentTrackMode.update { settings.trackMode }
            }.launchIn(viewModelScope)
    }

    fun setCurrentTrackMode(mode: MediaType) {
        viewModelScope.launch {
            _currentTrackMode.value = mode
        }
    }
}