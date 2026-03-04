package com.example.shikiflow.data.repository

import android.net.Uri
import android.util.Log
import com.example.shikiflow.data.datasource.AuthDataSource
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.repository.AuthRepository
import com.example.shikiflow.domain.repository.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val shikiAuthDataSource: AuthDataSource,
    private val anilistAuthDataSource: AuthDataSource,
    private val tokenRepository: TokenRepository,
    private val appRoomDatabase: AppRoomDatabase
) : AuthRepository {

    private fun getSource(authType: AuthType): AuthDataSource {
        return when(authType) {
            AuthType.SHIKIMORI -> shikiAuthDataSource
            AuthType.ANILIST -> anilistAuthDataSource
        }
    }

    override fun getAuthorizationUrl(authType: AuthType): String {
        return getSource(authType).getAuthorizationUrl()
    }

    override suspend fun handleAuthResponse(
        uriResponse: Uri,
        authType: AuthType
    ) {
        try {
            val tokenResponse = getSource(authType).handleAuthorizationResponse(uriResponse)

            tokenRepository.saveTokens(tokenResponse)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error retrieving tokens", e)
        }
    }

    override suspend fun logout() {
        tokenRepository.clearTokens()
        withContext(Dispatchers.IO) {
            appRoomDatabase.clearAllTables()
        }
    }
}