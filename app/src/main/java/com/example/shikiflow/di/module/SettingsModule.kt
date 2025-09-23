package com.example.shikiflow.di.module

import android.content.Context
import com.example.shikiflow.data.repository.CacheRepositoryImpl
import com.example.shikiflow.data.repository.SettingsRepositoryImpl
import com.example.shikiflow.domain.repository.CacheRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SettingsModule {

    @Provides
    @Singleton
    fun initializeCacheManager(
        @ApplicationContext context: Context
    ): CacheRepository { return CacheRepositoryImpl(context) }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository { return SettingsRepositoryImpl(context) }
}