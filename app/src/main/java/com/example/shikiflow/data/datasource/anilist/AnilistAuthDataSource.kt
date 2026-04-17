package com.example.shikiflow.data.datasource.anilist

import android.net.Uri
import android.util.Log
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.datasource.AuthDataSource
import com.example.shikiflow.domain.model.auth.AuthCredentials
import javax.inject.Inject

class AnilistAuthDataSource @Inject constructor(): AuthDataSource  {
    override fun getAuthorizationUrl(): String {
        return "${BuildConfig.ANILIST_BASE_URL}/api/v2/oauth/authorize" +
                "?client_id=${BuildConfig.ANILIST_CLIENT_ID}" +
                "&response_type=token"
    }

    override suspend fun handleAuthorizationResponse(uriResponse: Uri): AuthCredentials {
        Log.d("AnilistAuthDataSource", "Uri Response: $uriResponse")
        val accessToken = uriResponse.fragment?.split("&")
            ?.find { it.startsWith("access_token=") }
            ?.substringAfter("access_token=")

        return accessToken?.let {
            AuthCredentials(accessToken, null)
        } ?: throw IllegalStateException("Token response body is null")
    }
}