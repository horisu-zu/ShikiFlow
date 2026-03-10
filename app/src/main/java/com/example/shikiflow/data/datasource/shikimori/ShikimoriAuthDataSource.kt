package com.example.shikiflow.data.datasource.shikimori

import android.net.Uri
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.datasource.AuthDataSource
import com.example.shikiflow.data.remote.auth.ShikimoriAuthApi
import com.example.shikiflow.domain.model.auth.AuthCredentials
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

    override suspend fun handleAuthorizationResponse(uriResponse: Uri): AuthCredentials {
        val code = uriResponse.getQueryParameter("code")

        val tokenResponse = code?.let {
            shikiAuthApi.getAccessToken(code = code)
        }?.body()

        return tokenResponse?.let {
            AuthCredentials(
                accessToken = it.accessToken,
                refreshToken = it.refreshToken
            )
        } ?: throw IllegalStateException("Token response body is null")
    }
}