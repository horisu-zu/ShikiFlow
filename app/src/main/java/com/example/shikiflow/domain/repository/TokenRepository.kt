package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.auth.AuthCredentials
import com.example.shikiflow.domain.model.auth.TokenResponse
import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    val authCredentials: Flow<AuthCredentials>

    suspend fun saveTokens(tokenResponse: TokenResponse)
    suspend fun clearTokens()
}