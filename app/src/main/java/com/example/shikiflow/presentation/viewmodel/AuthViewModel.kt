package com.example.shikiflow.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.data.auth.AuthState
import com.example.shikiflow.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.isAuthenticated.collect { isAuth ->
                _authState.value = if (isAuth) {
                    Log.d("AuthViewModel", "Changing state to 'Success'")
                    AuthState.Success
                } else {
                    Log.d("AuthViewModel", "Changing state to 'Initial'")
                    AuthState.Initial
                }
            }
        }
    }

    fun getAuthorizationUrl() = authRepository.getAuthorizationUrl()

    fun handleAuthCode(code: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.handleAuthCode(code)
                .onSuccess { _authState.value = AuthState.Success }
                .onFailure { _authState.value = AuthState.Error(it.message ?: "Authentication failed") }
        }
    }
}