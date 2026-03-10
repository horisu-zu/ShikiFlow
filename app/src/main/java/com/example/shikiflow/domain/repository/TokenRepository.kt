package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.auth.AuthCredentials
import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    val authCredentials: Flow<AuthCredentials>

    suspend fun saveTokens(authCredentials: AuthCredentials)
    suspend fun clearTokens()
}