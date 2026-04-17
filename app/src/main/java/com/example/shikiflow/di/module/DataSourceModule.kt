package com.example.shikiflow.di.module

import com.example.shikiflow.data.datasource.AuthDataSource
import com.example.shikiflow.data.datasource.CharactersDataSource
import com.example.shikiflow.data.datasource.CommentsDataSource
import com.example.shikiflow.data.datasource.MediaDataSource
import com.example.shikiflow.data.datasource.MediaTracksDataSource
import com.example.shikiflow.data.datasource.StaffDataSource
import com.example.shikiflow.data.datasource.UserDataSource
import com.example.shikiflow.data.datasource.anilist.AnilistAuthDataSource
import com.example.shikiflow.data.datasource.anilist.AnilistCharactersDataSource
import com.example.shikiflow.data.datasource.anilist.AnilistMediaDataSource
import com.example.shikiflow.data.datasource.anilist.AnilistStaffDataSource
import com.example.shikiflow.data.datasource.anilist.AnilistThreadsDataSource
import com.example.shikiflow.data.datasource.anilist.AnilistTracksDataSource
import com.example.shikiflow.data.datasource.anilist.AnilistUserDataSource
import com.example.shikiflow.data.datasource.shikimori.ShikimoriAuthDataSource
import com.example.shikiflow.data.datasource.shikimori.ShikimoriCharactersDataSource
import com.example.shikiflow.data.datasource.shikimori.ShikimoriCommentsDataSource
import com.example.shikiflow.data.datasource.shikimori.ShikimoriMediaDataSource
import com.example.shikiflow.data.datasource.shikimori.ShikimoriStaffDataSource
import com.example.shikiflow.data.datasource.shikimori.ShikimoriTracksDataSource
import com.example.shikiflow.data.datasource.shikimori.ShikimoriUserDataSource
import com.example.shikiflow.di.annotations.AniList
import com.example.shikiflow.di.annotations.Shikimori
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {

    @Shikimori
    @Binds
    @Singleton
    fun bindShikimoriAuthDataSource(
        impl: ShikimoriAuthDataSource
    ): AuthDataSource

    //No need for Auth API cuz I implemented implicit grant
    @AniList
    @Binds
    @Singleton
    fun bindAnilistAuthDataSource(
        impl: AnilistAuthDataSource
    ): AuthDataSource

    @Shikimori
    @Binds
    @Singleton
    fun bindShikimoriAnimeTracksDataSource(
        impl: ShikimoriTracksDataSource
    ): MediaTracksDataSource

    @AniList
    @Binds
    @Singleton
    fun bindAnilistAnimeTracksDataSource(
        impl: AnilistTracksDataSource
    ): MediaTracksDataSource

    @Shikimori
    @Binds
    @Singleton
    fun bindShikimoriUserDataSource(
        impl: ShikimoriUserDataSource
    ): UserDataSource

    @AniList
    @Binds
    @Singleton
    fun bindAnilistUserDataSource(
        impl: AnilistUserDataSource
    ): UserDataSource

    @Shikimori
    @Binds
    @Singleton
    fun bindShikimoriMediaDetailsDataSource(
        impl: ShikimoriMediaDataSource
    ): MediaDataSource

    @AniList
    @Binds
    @Singleton
    fun bindAnilistMediaDetailsDataSource(
        impl: AnilistMediaDataSource
    ): MediaDataSource

    @Shikimori
    @Binds
    @Singleton
    fun bindShikimoriCharactersDataSource(
        impl: ShikimoriCharactersDataSource
    ): CharactersDataSource

    @AniList
    @Binds
    @Singleton
    fun bindAnilistCharactersDataSource(
        impl: AnilistCharactersDataSource
    ): CharactersDataSource

    @Shikimori
    @Binds
    @Singleton
    fun bindShikimoriStaffDataSource(
        impl: ShikimoriStaffDataSource
    ): StaffDataSource

    @AniList
    @Binds
    @Singleton
    fun bindAnilistStaffDataSource(
        impl: AnilistStaffDataSource
    ): StaffDataSource

    @Shikimori
    @Binds
    @Singleton
    fun bindShikimoriCommentsDataSource(
        impl: ShikimoriCommentsDataSource
    ): CommentsDataSource

    @AniList
    @Binds
    @Singleton
    fun bindAnilistCommentsDataSource(
        impl: AnilistThreadsDataSource
    ): CommentsDataSource
}