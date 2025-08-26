package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.auth.TokenResponse

interface AuthRepository {
    fun getAuthorizationUrl(): String
    suspend fun handleAuthCode(code: String): Result<TokenResponse>
    suspend fun logout()
}