package com.example.shikiflow.di.module

import com.apollographql.apollo.ApolloClient
import com.example.shikiflow.data.datasource.AuthDataSource
import com.example.shikiflow.data.datasource.CharactersDataSource
import com.example.shikiflow.data.datasource.CommentsDataSource
import com.example.shikiflow.data.datasource.MediaDetailsDataSource
import com.example.shikiflow.data.datasource.MediaTracksDataSource
import com.example.shikiflow.data.datasource.StaffDataSource
import com.example.shikiflow.data.datasource.UserDataSource
import com.example.shikiflow.data.datasource.anilist.AnilistAuthDataSource
import com.example.shikiflow.data.datasource.anilist.AnilistCharactersDataSource
import com.example.shikiflow.data.datasource.anilist.AnilistMediaDetailsDataSource
import com.example.shikiflow.data.datasource.anilist.AnilistStaffDataSource
import com.example.shikiflow.data.datasource.anilist.AnilistThreadsDataSource
import com.example.shikiflow.data.datasource.anilist.AnilistTracksDataSource
import com.example.shikiflow.data.datasource.anilist.AnilistUserDataSource
import com.example.shikiflow.data.datasource.shikimori.ShikimoriAuthDataSource
import com.example.shikiflow.data.datasource.shikimori.ShikimoriCharactersDataSource
import com.example.shikiflow.data.datasource.shikimori.ShikimoriCommentsDataSource
import com.example.shikiflow.data.datasource.shikimori.ShikimoriMediaDetailsDataSource
import com.example.shikiflow.data.datasource.shikimori.ShikimoriStaffDataSource
import com.example.shikiflow.data.datasource.shikimori.ShikimoriTracksDataSource
import com.example.shikiflow.data.datasource.shikimori.ShikimoriUserDataSource
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.remote.AnimeApi
import com.example.shikiflow.data.remote.CharacterApi
import com.example.shikiflow.data.remote.CommentApi
import com.example.shikiflow.data.remote.MangaApi
import com.example.shikiflow.data.remote.PersonApi
import com.example.shikiflow.data.remote.UserApi
import com.example.shikiflow.data.remote.auth.ShikimoriAuthApi
import com.example.shikiflow.di.annotations.AniList
import com.example.shikiflow.di.annotations.AnilistApollo
import com.example.shikiflow.di.annotations.Shikimori
import com.example.shikiflow.di.annotations.ShikimoriApollo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Shikimori
    @Provides
    @Singleton
    fun provideShikimoriAuthDataSource(
        shikiAuthApi: ShikimoriAuthApi
    ): AuthDataSource = ShikimoriAuthDataSource(shikiAuthApi)

    //No need for Auth API cuz I implemented implicit grant
    @AniList
    @Provides
    @Singleton
    fun provideAnilistAuthDataSource(): AuthDataSource = AnilistAuthDataSource()

    @Shikimori
    @Provides
    @Singleton
    fun provideShikimoriAnimeTracksDataSource(
        @ShikimoriApollo apolloClient: ApolloClient,
        appRoomDatabase: AppRoomDatabase
    ): MediaTracksDataSource = ShikimoriTracksDataSource(apolloClient, appRoomDatabase)

    @AniList
    @Provides
    @Singleton
    fun provideAnilistAnimeTracksDataSource(
        @AnilistApollo apolloClient: ApolloClient,
        appRoomDatabase: AppRoomDatabase
    ): MediaTracksDataSource = AnilistTracksDataSource(apolloClient, appRoomDatabase)

    @Shikimori
    @Provides
    @Singleton
    fun provideShikimoriUserDataSource(
        @ShikimoriApollo apolloClient: ApolloClient,
        userApi: UserApi
    ): UserDataSource = ShikimoriUserDataSource(apolloClient, userApi)

    @AniList
    @Provides
    @Singleton
    fun provideAnilistUserDataSource(
        @AnilistApollo apolloClient: ApolloClient
    ): UserDataSource = AnilistUserDataSource(apolloClient)

    @Shikimori
    @Provides
    @Singleton
    fun provideShikimoriMediaDetailsDataSource(
        @ShikimoriApollo apolloClient: ApolloClient,
        animeApi: AnimeApi,
        mangaApi: MangaApi
    ): MediaDetailsDataSource = ShikimoriMediaDetailsDataSource(apolloClient, animeApi, mangaApi)

    @AniList
    @Provides
    @Singleton
    fun provideAnilistMediaDetailsDataSource(
        @AnilistApollo apolloClient: ApolloClient
    ): MediaDetailsDataSource = AnilistMediaDetailsDataSource(apolloClient)

    @Shikimori
    @Provides
    @Singleton
    fun provideShikimoriCharactersDataSource(
        characterApi: CharacterApi,
        @ShikimoriApollo apolloClient: ApolloClient
    ): CharactersDataSource = ShikimoriCharactersDataSource(characterApi, apolloClient)

    @AniList
    @Provides
    @Singleton
    fun provideAnilistCharactersDataSource(
        @AnilistApollo apolloClient: ApolloClient
    ): CharactersDataSource = AnilistCharactersDataSource(apolloClient)

    @Shikimori
    @Provides
    @Singleton
    fun provideShikimoriStaffDataSource(
        personApi: PersonApi
    ): StaffDataSource = ShikimoriStaffDataSource(personApi)

    @AniList
    @Provides
    @Singleton
    fun provideAnilistStaffDataSource(
        @AnilistApollo apolloClient: ApolloClient
    ): StaffDataSource = AnilistStaffDataSource(apolloClient)

    @Shikimori
    @Provides
    @Singleton
    fun provideShikimoriCommentsDataSource(
        commentApi: CommentApi
    ): CommentsDataSource = ShikimoriCommentsDataSource(commentApi)

    @AniList
    @Provides
    @Singleton
    fun provideAnilistCommentsDataSource(
        @AnilistApollo apolloClient: ApolloClient
    ): CommentsDataSource = AnilistThreadsDataSource(apolloClient)
}