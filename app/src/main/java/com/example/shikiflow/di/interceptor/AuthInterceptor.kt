package com.example.shikiflow.di.interceptor

import android.util.Log
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.auth.TokenManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        Log.d("AuthInterceptor", "Original request URL: ${original.url}")

        val token = runBlocking {
            tokenManager.accessTokenFlow.firstOrNull()
        }
        Log.d("AuthInterceptor", "Token retrieved: $token")

        val request = original.newBuilder()
            .header("User-Agent", BuildConfig.USER_AGENT)
            .apply {
                if (!token.isNullOrBlank()) {
                    header("Authorization", "Bearer $token")
                    Log.d("AuthInterceptor", "Added Authorization header: Bearer $token")
                    Log.d("AuthInterceptor", "All headers: ${build().headers}")
                } else {
                    Log.e("AuthInterceptor", "No token found")
                }
            }
            .build()

        val response = chain.proceed(request)
        Log.d("AuthInterceptor", "Response code: ${response.code}")
        Log.d("AuthInterceptor", "Response body: ${response.peekBody(Long.MAX_VALUE).string()}")

        return response
    }
}