package com.example.shikiflow.data.repository

import android.util.Log
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.remote.auth.AnilistAuthApi
import com.example.shikiflow.data.remote.auth.ShikimoriAuthApi
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.repository.AuthRepository
import com.example.shikiflow.domain.repository.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val shikiAuthApi: ShikimoriAuthApi,
    private val anilistAuthApi: AnilistAuthApi,
    private val tokenRepository: TokenRepository,
    private val appRoomDatabase: AppRoomDatabase
) : AuthRepository {

    override fun getAuthorizationUrl(authType: AuthType): String {
        return when(authType) {
            AuthType.SHIKIMORI -> {
                "${BuildConfig.SHIKI_BASE_URL}/oauth/authorize" +
                        "?client_id=${BuildConfig.SHIKI_CLIENT_ID}" +
                        "&redirect_uri=${BuildConfig.SHIKI_REDIRECT_URI}" +
                        "&response_type=code" +
                        "&scope="
            }
            AuthType.ANILIST -> {
                "${BuildConfig.ANILIST_BASE_URL}/api/v2/oauth/authorize" +
                        "?client_id=${BuildConfig.ANILIST_CLIENT_ID}" +
                        "&redirect_uri=${BuildConfig.ANILIST_REDIRECT_URI}" +
                        "&response_type=code"
                /*"${BuildConfig.ANILIST_BASE_URL}/api/v2/oauth/authorize" +
                        "?client_id=${BuildConfig.ANILIST_CLIENT_ID}" +
                        "&response_type=token"*/
            }
        }
    }

    override suspend fun handleAuthCode(code: String, authType: AuthType) {
        try {
            /*val tokens = when(authType) {
                AuthType.SHIKIMORI -> {
                    val response = shikiAuthApi.getAccessToken(code = code)

                    response.body() ?: throw IllegalStateException("Token response body is null")
                }
                AuthType.ANILIST -> {
                    Log.d("AuthRepositoryImpl", "Response: $code")

                    TokenResponse(
                        accessToken = code,
                        refreshToken = null
                    )
                }
            }

            tokenRepository.saveTokens(tokens)*/
            val response = when(authType) {
                AuthType.SHIKIMORI -> shikiAuthApi.getAccessToken(code = code)
                AuthType.ANILIST -> anilistAuthApi.getAccessToken(
                    tokenRequest = AnilistAuthApi.AnilistTokenRequest(code = code)
                )
            }
            Log.d("AuthRepository", "Response: $response")

            if (response.isSuccessful) {
                val tokenResponse = response.body() ?: throw IllegalStateException("Token response body is null")

                Log.d("AuthRepository", "Token Response: $tokenResponse")
                tokenRepository.saveTokens(tokenResponse)
            } else {
                Log.d("AuthRepository", "Error: ${response.code()} - ${response.message()}. Body: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error during token operation", e)
        }
    }

    override suspend fun logout() {
        tokenRepository.clearTokens()
        withContext(Dispatchers.IO) {
            appRoomDatabase.clearAllTables()
        }
    }
}