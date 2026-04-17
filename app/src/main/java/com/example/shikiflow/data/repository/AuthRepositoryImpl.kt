package com.example.shikiflow.data.repository

import android.net.Uri
import android.util.Log
import com.example.shikiflow.data.datasource.AuthDataSource
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.di.annotations.AniList
import com.example.shikiflow.di.annotations.Shikimori
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.repository.AuthRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    @Shikimori private val shikiAuthDataSource: AuthDataSource,
    @AniList private val anilistAuthDataSource: AuthDataSource,
    private val tokenRepository: TokenRepository,
    private val settingsRepository: SettingsRepository,
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

            tokenRepository.saveTokens(tokenResponse, authType)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error retrieving tokens", e)
        }
    }

    override suspend fun logout() {
        withContext(Dispatchers.IO) {
            tokenRepository.clearTokens()
            settingsRepository.clearUserData()
            appRoomDatabase.clearAllTables()
        }
    }
}