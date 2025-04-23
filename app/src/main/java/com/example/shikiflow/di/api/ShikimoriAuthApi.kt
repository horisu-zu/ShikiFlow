package com.example.shikiflow.di.api

import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.auth.TokenResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ShikimoriAuthApi {
    @POST("oauth/token")
    @FormUrlEncoded
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("client_id") clientId: String = BuildConfig.CLIENT_ID,
        @Field("client_secret") clientSecret: String = BuildConfig.CLIENT_SECRET,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String = BuildConfig.REDIRECT_URI
    ): Response<TokenResponse>

    @POST("oauth/token")
    @FormUrlEncoded
    suspend fun refreshToken(
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("client_id") clientId: String = BuildConfig.CLIENT_ID,
        @Field("client_secret") clientSecret: String = BuildConfig.CLIENT_SECRET,
        @Field("refresh_token") refreshToken: String
    ): Response<TokenResponse>
}