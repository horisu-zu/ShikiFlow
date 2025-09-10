package com.example.shikiflow.di.interceptor

import android.util.Log
import com.example.shikiflow.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class KodikInterceptor @Inject constructor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        Log.d("KodikInterceptor", "Original request URL: ${original.url}")

        val token = BuildConfig.KODIK_API_TOKEN
        Log.d("KodikInterceptor", "Token retrieved: $token")

        val request = original.newBuilder()
            .header("User-Agent", BuildConfig.USER_AGENT)
            .build()

        val response = chain.proceed(request)
        Log.d("KodikInterceptor", "Response code: ${response.code}")
        Log.d("KodikInterceptor", "Response body: ${response.peekBody(Long.MAX_VALUE).string()}")

        return response
    }
}