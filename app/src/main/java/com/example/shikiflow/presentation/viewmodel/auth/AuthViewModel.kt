package com.example.shikiflow.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    fun getAuthorizationUrl(authType: AuthType): String {
        return authRepository.getAuthorizationUrl(authType)
    }
}