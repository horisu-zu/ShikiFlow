package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.auth.TokenResponse
import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    val tokensFlow: Flow<TokenResponse>

    suspend fun saveTokens(tokenResponse: TokenResponse)
    suspend fun clearTokens()
}