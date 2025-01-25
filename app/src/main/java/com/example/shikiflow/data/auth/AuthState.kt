package com.example.shikiflow.data.auth

sealed interface AuthState {
    object Initial : AuthState
    object Loading : AuthState
    object Success : AuthState
    data class Error(val message: String) : AuthState
}
