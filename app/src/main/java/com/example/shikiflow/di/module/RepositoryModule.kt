package com.example.shikiflow.di.module

import com.example.shikiflow.data.repository.AuthRepositoryImpl
import com.example.shikiflow.data.repository.CharacterRepositoryImpl
import com.example.shikiflow.data.repository.CommentRepositoryImpl
import com.example.shikiflow.data.repository.GithubRepositoryImpl
import com.example.shikiflow.data.repository.KodikRepositoryImpl
import com.example.shikiflow.data.repository.MangaDexRepositoryImpl
import com.example.shikiflow.data.repository.MediaRepositoryImpl
import com.example.shikiflow.data.repository.MediaTracksRepositoryImpl
import com.example.shikiflow.data.repository.StaffRepositoryImpl
import com.example.shikiflow.data.repository.UserRepositoryImpl
import com.example.shikiflow.domain.repository.AuthRepository
import com.example.shikiflow.domain.repository.CharacterRepository
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.domain.repository.GithubRepository
import com.example.shikiflow.domain.repository.KodikRepository
import com.example.shikiflow.domain.repository.MangaDexRepository
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.StaffRepository
import com.example.shikiflow.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    fun bindMediaTracksRepository(
        impl: MediaTracksRepositoryImpl
    ): MediaTracksRepository

    @Binds
    @Singleton
    fun bindMediaDetailsRepository(
        impl: MediaRepositoryImpl
    ): MediaRepository

    @Binds
    @Singleton
    fun bindMangaDexRepository(
        impl: MangaDexRepositoryImpl
    ): MangaDexRepository

    @Binds
    @Singleton
    fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    fun bindCharacterRepository(
        impl: CharacterRepositoryImpl
    ): CharacterRepository

    @Binds
    @Singleton
    fun bindStaffRepository(
        impl: StaffRepositoryImpl
    ): StaffRepository

    @Binds
    @Singleton
    fun bindCommentRepository(
        impl: CommentRepositoryImpl
    ): CommentRepository

    @Binds
    @Singleton
    fun bindGithubRepository(
        impl: GithubRepositoryImpl
    ): GithubRepository

    @Binds
    @Singleton
    fun bindKodikRepository(
        impl: KodikRepositoryImpl
    ): KodikRepository
}