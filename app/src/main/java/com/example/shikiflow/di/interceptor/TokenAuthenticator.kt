package com.example.shikiflow.di.interceptor

import android.util.Log
import com.example.shikiflow.data.remote.ShikimoriAuthApi
import com.example.shikiflow.domain.auth.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApi: ShikimoriAuthApi
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {

        val refreshToken = runBlocking {
            tokenManager.refreshTokenFlow.firstOrNull()
        }

        return try {
            runBlocking(Dispatchers.IO) {
                val tokenResponse = refreshToken?.let {
                    Log.d("TokenAuthenticator", "Refresh Token: $refreshToken")
                    authApi.refreshToken(refreshToken = it).body()
                }
                tokenResponse?.let {
                    tokenManager.saveTokens(it)
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${it.accessToken}")
                        .build()
                }
            }
        } catch (e: Exception) {
            Log.e("TokenAuthenticator", "Error refreshing token", e)
            null
        }
    }
}