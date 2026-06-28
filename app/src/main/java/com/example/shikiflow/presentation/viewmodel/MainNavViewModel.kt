package com.example.shikiflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainNavViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _authEvent = MutableSharedFlow<AuthType>()
    val authEvent = _authEvent.asSharedFlow()

    val preferredTitleType = settingsRepository.userSettingsFlow
        .mapNotNull { settings ->
            settings?.preferredTitleType
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = PreferredTitleType.ROMAJI
        )

    init {
        settingsRepository.authTypeFlow
            .filterNotNull()
            .distinctUntilChanged()
            .drop(1)
            .onEach { authType ->
                _authEvent.emit(authType)
            }.launchIn(viewModelScope)
    }
}