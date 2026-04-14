package com.example.shikiflow.di.module

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo.cache.normalized.normalizedCache
import com.apollographql.apollo.network.okHttpClient
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.di.annotations.GithubOkHttpClient
import com.example.shikiflow.di.annotations.GithubRetrofit
import com.example.shikiflow.di.annotations.ShikimoriRetrofit
import com.example.shikiflow.data.remote.AnimeApi
import com.example.shikiflow.data.remote.CharacterApi
import com.example.shikiflow.data.remote.CommentApi
import com.example.shikiflow.data.remote.GithubApi
import com.example.shikiflow.data.remote.KodikApi
import com.example.shikiflow.data.remote.MangaApi
import com.example.shikiflow.data.remote.MangaDexApi
import com.example.shikiflow.data.remote.PersonApi
import com.example.shikiflow.data.remote.UserApi
import com.example.shikiflow.di.annotations.AnilistApollo
import com.example.shikiflow.di.annotations.AnilistOkHttpClient
import com.example.shikiflow.di.annotations.KodikOkHttpClient
import com.example.shikiflow.di.annotations.KodikRetrofit
import com.example.shikiflow.di.annotations.MainOkHttpClient
import com.example.shikiflow.di.annotations.MangaDexRetrofit
import com.example.shikiflow.di.annotations.ShikimoriApollo
import com.example.shikiflow.di.annotations.ShikimoriOkHttpClient
import com.example.shikiflow.di.interceptor.AuthInterceptor
import com.example.shikiflow.di.interceptor.KodikInterceptor
import com.example.shikiflow.di.interceptor.TokenAuthenticator
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    @MainOkHttpClient
    fun provideMainOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    @ShikimoriOkHttpClient
    fun provideShikimoriOkHttpClient(
        tokenRepository: TokenRepository,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenRepository, AuthType.SHIKIMORI))
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    @AnilistOkHttpClient
    fun provideAnilistOkHttpClient(
        tokenRepository: TokenRepository
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenRepository, AuthType.ANILIST))
            .addInterceptor(logging)
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
                    //.header("Authorization", "Bearer ${BuildConfig.GITHUB_TOKEN}")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    @KodikOkHttpClient
    fun provideKodikOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(KodikInterceptor())
            .build()
    }

    @Provides
    @Singleton
    @ShikimoriRetrofit
    fun provideMainRetrofit(
        @ShikimoriOkHttpClient okHttpClient: OkHttpClient,
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
    @MangaDexRetrofit
    fun provideMangaDexRetrofit(
        @MainOkHttpClient okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.MANGADEX_URL)
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
    @KodikRetrofit
    fun provideKodikRetrofit(
        @KodikOkHttpClient okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.KODIK_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    @ShikimoriApollo
    fun provideShikimoriApolloClient(
        @ShikimoriOkHttpClient okHttpClient: OkHttpClient
    ): ApolloClient {
        val cacheFactory = MemoryCacheFactory(10 * 1024 * 1024)

        return ApolloClient.Builder()
            .serverUrl("${BuildConfig.SHIKI_BASE_URL}/api/graphql")
            .okHttpClient(okHttpClient)
            .normalizedCache(cacheFactory)
            .build()
    }

    @Provides
    @Singleton
    @AnilistApollo
    fun provideAnilistApolloClient(
        @AnilistOkHttpClient okHttpClient: OkHttpClient
    ): ApolloClient {
        val cacheFactory = MemoryCacheFactory(10 * 1024 * 1024)

        return ApolloClient.Builder()
            .serverUrl(BuildConfig.ANILIST_GRAPHQL_URL)
            .okHttpClient(okHttpClient)
            .normalizedCache(cacheFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserApi(@ShikimoriRetrofit retrofit: Retrofit): UserApi =
        retrofit.create()

    @Provides
    @Singleton
    fun provideCharacterApi(@ShikimoriRetrofit retrofit: Retrofit): CharacterApi =
        retrofit.create()

    @Provides
    @Singleton
    fun providePersonApi(@ShikimoriRetrofit retrofit: Retrofit): PersonApi =
        retrofit.create()

    @Provides
    @Singleton
    fun provideGithubApi(@GithubRetrofit retrofit: Retrofit): GithubApi =
        retrofit.create()

    @Provides
    @Singleton
    fun provideAnimeApi(@ShikimoriRetrofit retrofit: Retrofit): AnimeApi =
        retrofit.create()

    @Provides
    @Singleton
    fun provideMangaApi(@ShikimoriRetrofit retrofit: Retrofit): MangaApi =
        retrofit.create()

    @Provides
    @Singleton
    fun provideCommentApi(@ShikimoriRetrofit retrofit: Retrofit): CommentApi =
        retrofit.create()

    @Provides
    @Singleton
    fun provideMangaDexApi(@MangaDexRetrofit retrofit: Retrofit): MangaDexApi =
        retrofit.create()

    @Provides
    @Singleton
    fun provideKodikApi(@KodikRetrofit retrofit: Retrofit): KodikApi = retrofit.create()
}