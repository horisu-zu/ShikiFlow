package com.example.shikiflow.di.module

import android.content.Context
import com.example.shikiflow.data.datasource.AuthDataSource
import com.example.shikiflow.data.datasource.CharactersDataSource
import com.example.shikiflow.data.datasource.CommentsDataSource
import com.example.shikiflow.data.datasource.MediaDetailsDataSource
import com.example.shikiflow.data.datasource.MediaTracksDataSource
import com.example.shikiflow.data.datasource.StaffDataSource
import com.example.shikiflow.data.datasource.UserDataSource
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.remote.GithubApi
import com.example.shikiflow.data.remote.KodikApi
import com.example.shikiflow.data.remote.MangaDexApi
import com.example.shikiflow.data.repository.AuthRepositoryImpl
import com.example.shikiflow.data.repository.CharacterRepositoryImpl
import com.example.shikiflow.data.repository.CommentRepositoryImpl
import com.example.shikiflow.data.repository.GithubRepositoryImpl
import com.example.shikiflow.data.repository.KodikRepositoryImpl
import com.example.shikiflow.data.repository.MangaDexRepositoryImpl
import com.example.shikiflow.data.repository.MediaRepositoryImpl
import com.example.shikiflow.data.repository.MediaTracksRepositoryImpl
import com.example.shikiflow.data.repository.StaffRepositoryImpl
import com.example.shikiflow.data.repository.TokenRepositoryImpl
import com.example.shikiflow.data.repository.UserRepositoryImpl
import com.example.shikiflow.di.annotations.AniList
import com.example.shikiflow.di.annotations.Shikimori
import com.example.shikiflow.domain.repository.AuthRepository
import com.example.shikiflow.domain.repository.CharacterRepository
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.domain.repository.GithubRepository
import com.example.shikiflow.domain.repository.KodikRepository
import com.example.shikiflow.domain.repository.MangaDexRepository
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.StaffRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.TokenRepository
import com.example.shikiflow.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        @Shikimori shikiAuthDataSource: AuthDataSource,
        @AniList anilistAuthDataSource: AuthDataSource,
        tokenRepository: TokenRepository,
        settingsRepository: SettingsRepository,
        appRoomDatabase: AppRoomDatabase
    ): AuthRepository = AuthRepositoryImpl(shikiAuthDataSource, anilistAuthDataSource, tokenRepository, settingsRepository, appRoomDatabase)

    @Provides
    @Singleton
    fun provideTokenRepository(
        @ApplicationContext context: Context
    ): TokenRepository = TokenRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideMediaTracksRepository(
        @Shikimori shikimoriTracksDataSource: MediaTracksDataSource,
        @AniList anilistTracksDataSource: MediaTracksDataSource,
        settingsRepository: SettingsRepository,
        appRoomDatabase: AppRoomDatabase
    ): MediaTracksRepository = MediaTracksRepositoryImpl(shikimoriTracksDataSource, anilistTracksDataSource, settingsRepository, appRoomDatabase)

    @Provides
    @Singleton
    fun provideMediaDetailsRepository(
        @Shikimori shikimoriDataSource: MediaDetailsDataSource,
        @AniList anilistDataSource: MediaDetailsDataSource,
        settingsRepository: SettingsRepository,
    ): MediaRepository = MediaRepositoryImpl(anilistDataSource, shikimoriDataSource, settingsRepository)

    @Provides
    @Singleton
    fun provideMangaDexRepository(
        mangaDexApi: MangaDexApi
    ): MangaDexRepository = MangaDexRepositoryImpl(mangaDexApi)

    @Provides
    @Singleton
    fun provideUserRepository(
        @Shikimori shikimoriUserDataSource: UserDataSource,
        @AniList anilistUserDataSource: UserDataSource,
        settingsRepository: SettingsRepository
    ): UserRepository = UserRepositoryImpl(shikimoriUserDataSource, anilistUserDataSource, settingsRepository)

    @Provides
    @Singleton
    fun provideCharacterRepository(
        @Shikimori shikimoriDataSource: CharactersDataSource,
        @AniList anilistDataSource: CharactersDataSource,
        settingsRepository: SettingsRepository
    ): CharacterRepository = CharacterRepositoryImpl(shikimoriDataSource, anilistDataSource, settingsRepository)

    @Provides
    @Singleton
    fun providePersonRepository(
        @Shikimori shikimoriDataSource: StaffDataSource,
        @AniList anilistDataSource: StaffDataSource,
        settingsRepository: SettingsRepository
    ): StaffRepository = StaffRepositoryImpl(anilistDataSource, shikimoriDataSource, settingsRepository)

    @Provides
    @Singleton
    fun provideCommentRepository(
        @Shikimori shikimoriDataSource: CommentsDataSource,
        @AniList anilistDataSource: CommentsDataSource,
        settingsRepository: SettingsRepository
    ): CommentRepository = CommentRepositoryImpl(shikimoriDataSource, anilistDataSource, settingsRepository)

    @Provides
    @Singleton
    fun provideGithubRepository(
        githubApi: GithubApi
    ): GithubRepository = GithubRepositoryImpl(githubApi)

    @Provides
    @Singleton
    fun provideKodikRepository(
        kodikApi: KodikApi
    ): KodikRepository = KodikRepositoryImpl(kodikApi)
}