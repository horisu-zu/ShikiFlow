package com.example.shikiflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.search.ScreenSearchState
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.model.tracks.MediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _currentTrackMode = MutableStateFlow<MediaType?>(null)
    val currentTrackMode = _currentTrackMode.asStateFlow()

    private val _screenState = MutableStateFlow(ScreenSearchState())
    val screenState: StateFlow<ScreenSearchState> = _screenState.asStateFlow()

    val searchQuery = _screenState
        .map { it.query }
        .debounce(500L)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

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

    fun onQueryChange(newQuery: String) {
        _screenState.update { it.copy(query = newQuery) }
    }

    fun onSearchActiveChange(isActive: Boolean) {
        _screenState.update { it.copy(isSearchActive = isActive) }
    }

    fun exitSearchState() {
        _screenState.update { it.copy(
            isSearchActive = false,
            query = ""
        ) }
    }
}