package com.example.shikiflow.di.module

import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.remote.auth.AnilistAuthApi
import com.example.shikiflow.di.annotations.ShikiAuthRetrofit
import com.example.shikiflow.data.remote.auth.ShikimoriAuthApi
import com.example.shikiflow.di.annotations.AnilistAuthRetrofit
import com.example.shikiflow.di.annotations.AuthOkHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthNetworkModule {

    @Provides
    @Singleton
    @AuthOkHttpClient
    fun provideShikimoriAuthOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("User-Agent", BuildConfig.USER_AGENT)
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    @ShikiAuthRetrofit
    fun provideShikiAuthRetrofit(
        @AuthOkHttpClient okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SHIKI_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    @AnilistAuthRetrofit
    fun provideAnilistAuthRetrofit(
        @AuthOkHttpClient okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.ANILIST_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideShikiAuthApi(@ShikiAuthRetrofit retrofit: Retrofit): ShikimoriAuthApi =
        retrofit.create()

    @Provides
    @Singleton
    fun provideAnilistAuthApi(@AnilistAuthRetrofit retrofit: Retrofit): AnilistAuthApi =
        retrofit.create()
}