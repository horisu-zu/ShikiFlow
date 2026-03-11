package com.example.shikiflow.data.datasource

import android.net.Uri
import com.example.shikiflow.domain.model.auth.AuthCredentials

interface AuthDataSource {
    fun getAuthorizationUrl(): String
    suspend fun handleAuthorizationResponse(uriResponse: Uri): AuthCredentials
}