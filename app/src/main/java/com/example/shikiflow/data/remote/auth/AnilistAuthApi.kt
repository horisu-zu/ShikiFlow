package com.example.shikiflow.data.remote.auth

import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.model.auth.TokenResponse
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AnilistAuthApi {
    @POST("api/v2/oauth/token")
    suspend fun getAccessToken(
        @Body tokenRequest: AnilistTokenRequest
    ): Response<TokenResponse>

    @Serializable
    data class AnilistTokenRequest(
        val grant_type: String = "authorization_code",
        val client_id: String = BuildConfig.ANILIST_CLIENT_ID,
        val client_secret: String = BuildConfig.ANILIST_CLIENT_SECRET,
        val redirect_uri: String = BuildConfig.ANILIST_REDIRECT_URI,
        val code: String
    )
}

