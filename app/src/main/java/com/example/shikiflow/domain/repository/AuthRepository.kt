package com.example.shikiflow.domain.repository

import android.net.Uri
import com.example.shikiflow.domain.model.auth.AuthType

interface AuthRepository {
    fun getAuthorizationUrl(authType: AuthType): String
    suspend fun handleAuthResponse(uriResponse: Uri, authType: AuthType)
    suspend fun logout()
}