package com.example.shikiflow.di.module

import com.apollographql.apollo.ApolloClient
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.local.dao.AnimeTracksDao
import com.example.shikiflow.data.local.dao.MangaTracksDao
import com.example.shikiflow.data.remote.ShikimoriAuthApi
import com.example.shikiflow.data.repository.AnimeTracksRepositoryImpl
import com.example.shikiflow.data.repository.AuthRepositoryImpl
import com.example.shikiflow.data.repository.MangaTracksRepositoryImpl
import com.example.shikiflow.domain.auth.TokenManager
import com.example.shikiflow.domain.repository.AnimeTracksRepository
import com.example.shikiflow.domain.repository.AuthRepository
import com.example.shikiflow.domain.repository.MangaTracksRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: ShikimoriAuthApi,
        tokenManager: TokenManager
    ): AuthRepository = AuthRepositoryImpl(authApi, tokenManager)

    @Provides
    @Singleton
    fun provideAnimeTracksRepository(
        apolloClient: ApolloClient,
        appRoomDatabase: AppRoomDatabase,
        animeTracksDao: AnimeTracksDao
    ): AnimeTracksRepository = AnimeTracksRepositoryImpl(apolloClient, appRoomDatabase, animeTracksDao)


    @Provides
    @Singleton
    fun provideMangaTracksRepository(
        apolloClient: ApolloClient,
        appRoomDatabase: AppRoomDatabase,
        mangaTracksDao: MangaTracksDao
    ): MangaTracksRepository = MangaTracksRepositoryImpl(apolloClient, appRoomDatabase, mangaTracksDao)
}