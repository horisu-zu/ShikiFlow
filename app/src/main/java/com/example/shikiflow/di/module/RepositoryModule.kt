package com.example.shikiflow.di.module

import com.apollographql.apollo.ApolloClient
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.local.dao.AnimeTracksDao
import com.example.shikiflow.data.local.dao.MangaTracksDao
import com.example.shikiflow.data.remote.AnimeApi
import com.example.shikiflow.data.remote.CharacterApi
import com.example.shikiflow.data.remote.CommentApi
import com.example.shikiflow.data.remote.GithubApi
import com.example.shikiflow.data.remote.MangaApi
import com.example.shikiflow.data.remote.MangaDexApi
import com.example.shikiflow.data.remote.ShikimoriAuthApi
import com.example.shikiflow.data.remote.UserApi
import com.example.shikiflow.data.repository.AnimeRepositoryImpl
import com.example.shikiflow.data.repository.AnimeTracksRepositoryImpl
import com.example.shikiflow.data.repository.AuthRepositoryImpl
import com.example.shikiflow.data.repository.CharacterRepositoryImpl
import com.example.shikiflow.data.repository.CommentRepositoryImpl
import com.example.shikiflow.data.repository.GithubRepositoryImpl
import com.example.shikiflow.data.repository.MangaDexRepositoryImpl
import com.example.shikiflow.data.repository.MangaRepositoryImpl
import com.example.shikiflow.data.repository.MangaTracksRepositoryImpl
import com.example.shikiflow.data.repository.UserRepositoryImpl
import com.example.shikiflow.domain.auth.TokenManager
import com.example.shikiflow.domain.repository.AnimeRepository
import com.example.shikiflow.domain.repository.AnimeTracksRepository
import com.example.shikiflow.domain.repository.AuthRepository
import com.example.shikiflow.domain.repository.CharacterRepository
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.domain.repository.GithubRepository
import com.example.shikiflow.domain.repository.MangaDexRepository
import com.example.shikiflow.domain.repository.MangaRepository
import com.example.shikiflow.domain.repository.MangaTracksRepository
import com.example.shikiflow.domain.repository.UserRepository
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

    @Provides
    @Singleton
    fun provideAnimeRepository(
        apolloClient: ApolloClient,
        animeApi: AnimeApi
    ): AnimeRepository = AnimeRepositoryImpl(apolloClient, animeApi)

    @Provides
    @Singleton
    fun provideMangaRepository(
        apolloClient: ApolloClient,
        mangaApi: MangaApi
    ): MangaRepository = MangaRepositoryImpl(apolloClient, mangaApi)

    @Provides
    @Singleton
    fun provideMangaDexRepository(
        mangaDexApi: MangaDexApi
    ): MangaDexRepository = MangaDexRepositoryImpl(mangaDexApi)

    @Provides
    @Singleton
    fun provideUserRepository(
        apolloClient: ApolloClient,
        userApi: UserApi
    ): UserRepository = UserRepositoryImpl(apolloClient, userApi)

    @Provides
    @Singleton
    fun provideCharacterRepository(
        apolloClient: ApolloClient,
        characterApi: CharacterApi
    ): CharacterRepository = CharacterRepositoryImpl(characterApi)

    @Provides
    @Singleton
    fun provideCommentRepository(
        commentApi: CommentApi
    ): CommentRepository = CommentRepositoryImpl(commentApi)

    @Provides
    @Singleton
    fun provideGithubRepository(
        githubApi: GithubApi
    ): GithubRepository = GithubRepositoryImpl(githubApi)
}