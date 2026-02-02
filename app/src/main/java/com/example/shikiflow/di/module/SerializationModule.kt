package com.example.shikiflow.di.module

import com.example.shikiflow.data.datasource.dto.InstantSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton
import kotlin.time.Instant

@Module
@InstallIn(SingletonComponent::class)
object SerializationModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
        encodeDefaults = true
        serializersModule = kotlinx.serialization.modules.SerializersModule {
            contextual(Instant::class, InstantSerializer)
        }
    }
}
