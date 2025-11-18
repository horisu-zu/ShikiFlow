package com.example.shikiflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.repository.AuthRepository
import com.example.shikiflow.domain.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val authRepository: AuthRepository
): ViewModel() {

    val authState: StateFlow<AuthState> = tokenRepository.tokensFlow
        .map { tokens ->
            when(tokens.accessToken) {
                null -> AuthState.Initial
                else -> AuthState.Success
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AuthState.Loading
        )

    fun getAuthorizationUrl() = authRepository.getAuthorizationUrl()

    fun handleAuthCode(code: String) {
        viewModelScope.launch {
            authRepository.handleAuthCode(code)
        }
    }
}

sealed interface AuthState {
    object Initial : AuthState
    object Loading : AuthState
    object Success : AuthState
    data class Error(val message: String) : AuthState
}