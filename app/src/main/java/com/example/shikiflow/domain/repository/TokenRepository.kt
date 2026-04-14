package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.auth.AuthCredentials
import com.example.shikiflow.domain.model.auth.AuthType
import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    fun authCredentials(authType: AuthType): Flow<AuthCredentials>

    suspend fun saveTokens(authCredentials: AuthCredentials, authType: AuthType)
    suspend fun clearTokens()
}