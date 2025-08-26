package com.example.shikiflow.data.repository

import android.util.Log
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.remote.ShikimoriAuthApi
import com.example.shikiflow.domain.model.auth.TokenResponse
import com.example.shikiflow.domain.auth.TokenManager
import com.example.shikiflow.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: ShikimoriAuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {
    override fun getAuthorizationUrl(): String {
        return "${BuildConfig.BASE_URL}/oauth/authorize" +
                "?client_id=${BuildConfig.CLIENT_ID}" +
                "&redirect_uri=${BuildConfig.REDIRECT_URI}" +
                "&response_type=code" +
                "&scope="
    }

    override suspend fun handleAuthCode(code: String): Result<TokenResponse> {
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

    override suspend fun logout() {
        tokenManager.clearTokens()
    }
}