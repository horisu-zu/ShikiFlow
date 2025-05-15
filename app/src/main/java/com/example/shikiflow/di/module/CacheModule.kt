package com.example.shikiflow.di.module

import android.content.Context
import com.example.shikiflow.utils.CacheManager
import com.example.shikiflow.utils.CacheManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class CacheModule {

    @Provides
    @Singleton
    fun initializeCacheManager(
        @ApplicationContext context: Context
    ): CacheManager { return CacheManagerImpl(context) }
}