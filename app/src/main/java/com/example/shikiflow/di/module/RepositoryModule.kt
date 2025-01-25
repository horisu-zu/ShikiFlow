package com.example.shikiflow.di.module

import com.example.shikiflow.di.api.ShikimoriAuthApi
import com.example.shikiflow.domain.auth.TokenManager
import com.example.shikiflow.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: ShikimoriAuthApi,
        tokenManager: TokenManager
    ): AuthRepository = AuthRepository(authApi, tokenManager)
}