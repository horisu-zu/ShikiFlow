package com.example.shikiflow.di.module

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.di.annotations.GithubOkHttpClient
import com.example.shikiflow.di.annotations.GithubRetrofit
import com.example.shikiflow.di.annotations.MainOkHttpClient
import com.example.shikiflow.di.annotations.MainRetrofit
import com.example.shikiflow.di.api.CharacterApi
import com.example.shikiflow.di.api.GithubApi
import com.example.shikiflow.di.api.UserApi
import com.example.shikiflow.di.interceptor.AuthInterceptor
import com.example.shikiflow.di.interceptor.TokenAuthenticator
import com.example.shikiflow.domain.auth.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    @MainOkHttpClient
    fun provideMainOkHttpClient(
        tokenManager: TokenManager,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    @GithubOkHttpClient
    fun provideGithubOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("Authorization", "Bearer ${BuildConfig.GITHUB_API_TOKEN}")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    @MainRetrofit
    fun provideMainRetrofit(
        @MainOkHttpClient okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    @GithubRetrofit
    fun provideGithubRetrofit(
        @GithubOkHttpClient okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.GITHUB_API_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideApolloClient(
        @MainOkHttpClient okHttpClient: OkHttpClient
    ): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("${BuildConfig.BASE_URL}/api/graphql")
            .okHttpClient(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        @MainOkHttpClient okHttpClient: OkHttpClient
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = {
                            okHttpClient
                        }
                    )
                )
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideUserApi(@MainRetrofit retrofit: Retrofit): UserApi =
        retrofit.create()

    @Provides
    @Singleton
    fun provideCharacterApi(@MainRetrofit retrofit: Retrofit): CharacterApi =
        retrofit.create()

    @Provides
    @Singleton
    fun provideGithubApi(@GithubRetrofit retrofit: Retrofit): GithubApi =
        retrofit.create()
}