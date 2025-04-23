package com.example.shikiflow.domain.repository

import android.util.Log
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.auth.TokenResponse
import com.example.shikiflow.di.api.ShikimoriAuthApi
import com.example.shikiflow.domain.auth.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class AuthRepository(
    private val authApi: ShikimoriAuthApi,
    private val tokenManager: TokenManager
) {
    val isAuthenticated: Flow<Boolean> = tokenManager.accessTokenFlow
        .map { it != null }
        .distinctUntilChanged()

    fun getAuthorizationUrl(): String {
        return "${BuildConfig.BASE_URL}/oauth/authorize" +
                "?client_id=${BuildConfig.CLIENT_ID}" +
                "&redirect_uri=${BuildConfig.REDIRECT_URI}" +
                "&response_type=code" +
                "&scope="
    }

    suspend fun handleAuthCode(code: String): Result<TokenResponse> {
        return try {
            val response = authApi.getAccessToken(code = code)
            if (response.isSuccessful) {
                val tokenResponse = response.body()
                    ?: return Result.failure(IllegalStateException("Empty response body"))
                tokenManager.saveTokens(tokenResponse)
                Result.success(tokenResponse)
            } else {
                Result.failure(
                    IllegalStateException(
                        "Error: ${response.code()} - ${response.message()}. Body: ${response.errorBody()?.string()}"
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error during token operation", e)
            Result.failure(e)
        }
    }

    /*suspend fun refreshToken(): Result<TokenResponse> {
        return try {
            val currentRefreshToken = tokenManager.getRefreshToken()
                ?: return Result.failure(IllegalStateException("No refresh token available"))

            val response = authApi.refreshToken(refreshToken = currentRefreshToken)
            if (response.isSuccessful) {
                val tokenResponse = response.body()
                    ?: return Result.failure(IllegalStateException("Empty response body"))
                tokenManager.saveTokens(tokenResponse)
                Result.success(tokenResponse)
            } else {
                Result.failure(
                    IllegalStateException(
                        "Error: ${response.code()} - ${response.message()}. Body: ${response.errorBody()?.string()}"
                    )
                )
            }
        } catch (e: Exception) {
            tokenManager.clearTokens()
            Result.failure(e)
        }
    }*/

    suspend fun logout() {
        tokenManager.clearTokens()
    }
}
