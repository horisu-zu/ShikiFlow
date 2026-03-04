package com.example.shikiflow.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.settings.ThemeSettings
import com.example.shikiflow.domain.repository.AuthRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository
): ViewModel() {

    val _authType = MutableStateFlow<AuthType?>(null)

    val themeSettings = settingsRepository.themeSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ThemeSettings()
        )

    val authState: StateFlow<AuthState> = tokenRepository.authCredentials
        .map { tokens ->
            when(tokens.accessToken) {
                null -> AuthState.Initial
                else -> AuthState.Success
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AuthState.Loading
        )

    fun getAuthorizationUrl(authType: AuthType): String {
        _authType.value = authType
        return authRepository.getAuthorizationUrl(authType)
    }

    fun handleAuthCode(uriResponse: Uri) {
        viewModelScope.launch {
            _authType.value?.let { type ->
                authRepository.handleAuthResponse(uriResponse, type)
                settingsRepository.saveAuthType(type)
            }
        }
    }
}

sealed interface AuthState {
    object Initial : AuthState
    object Loading : AuthState
    object Success : AuthState
    data class Error(val message: String) : AuthState
}