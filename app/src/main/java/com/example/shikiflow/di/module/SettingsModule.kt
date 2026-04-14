package com.example.shikiflow.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.shikiflow.data.repository.CacheRepositoryImpl
import com.example.shikiflow.data.repository.SettingsRepositoryImpl
import com.example.shikiflow.data.repository.TokenRepositoryImpl
import com.example.shikiflow.domain.repository.CacheRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.TokenRepository
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
    ): SettingsRepository { return SettingsRepositoryImpl(providePreferencesDataStore(context, "app_settings")) }

    @Provides
    @Singleton
    fun provideTokenRepository(
        @ApplicationContext context: Context
    ): TokenRepository = TokenRepositoryImpl(providePreferencesDataStore(context, "auth"))

    private fun providePreferencesDataStore(
        appContext: Context,
        fileName: String
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { appContext.preferencesDataStoreFile(fileName) }
        )
    }
}