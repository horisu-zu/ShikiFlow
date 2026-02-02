package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.auth.AuthType

interface AuthRepository {
    fun getAuthorizationUrl(authType: AuthType): String
    suspend fun handleAuthCode(code: String, authType: AuthType)
    suspend fun logout()
}