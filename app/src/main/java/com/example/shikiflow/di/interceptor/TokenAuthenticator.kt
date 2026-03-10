package com.example.shikiflow.di.interceptor

import android.util.Log
import com.example.shikiflow.data.remote.auth.ShikimoriAuthApi
import com.example.shikiflow.domain.model.auth.AuthCredentials
import com.example.shikiflow.domain.repository.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val shikiAuthApi: ShikimoriAuthApi
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {

        val refreshToken = runBlocking {
            tokenRepository.authCredentials.firstOrNull()?.refreshToken
        }

        return try {
            runBlocking(Dispatchers.IO) {
                val tokenResponse = refreshToken?.let {
                    Log.d("TokenAuthenticator", "Refresh Token: $refreshToken")
                    shikiAuthApi.refreshToken(refreshToken = it).body()
                }
                tokenResponse?.let {
                    tokenRepository.saveTokens(AuthCredentials(it.accessToken, it.refreshToken))
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