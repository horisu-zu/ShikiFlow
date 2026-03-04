package com.example.shikiflow.data.datasource.shikimori

import android.net.Uri
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.datasource.AuthDataSource
import com.example.shikiflow.data.remote.auth.ShikimoriAuthApi
import com.example.shikiflow.domain.model.auth.TokenResponse
import javax.inject.Inject

class ShikimoriAuthDataSource @Inject constructor(
    private val shikiAuthApi: ShikimoriAuthApi,
): AuthDataSource {
    override fun getAuthorizationUrl(): String {
        return "${BuildConfig.SHIKI_BASE_URL}/oauth/authorize" +
                "?client_id=${BuildConfig.SHIKI_CLIENT_ID}" +
                "&redirect_uri=${BuildConfig.SHIKI_REDIRECT_URI}" +
                "&response_type=code" +
                "&scope="
    }

    override suspend fun handleAuthorizationResponse(uriResponse: Uri): TokenResponse {
        val code = uriResponse.getQueryParameter("code")

        return code?.let {
            shikiAuthApi.getAccessToken(code = code)
        }?.body() ?: throw IllegalStateException("Token response body is null")
    }
}