package com.example.shikiflow.di.module

import android.content.Context
import com.example.shikiflow.data.remote.UserApi
import com.example.shikiflow.data.uploader.CatBoxUploader
import com.example.shikiflow.data.uploader.MediaUploader
import com.example.shikiflow.data.uploader.ShikimoriUploader
import com.example.shikiflow.di.annotations.CatBoxMediaUploader
import com.example.shikiflow.di.annotations.MainOkHttpClient
import com.example.shikiflow.di.annotations.ShikimoriMediaUploader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UploaderModule {

    @CatBoxMediaUploader
    @Provides
    @Singleton
    fun provideMediaUploader(
        @ApplicationContext context: Context,
        @MainOkHttpClient okHttpClient: OkHttpClient
    ): MediaUploader = CatBoxUploader(context, okHttpClient)

    @ShikimoriMediaUploader
    @Provides
    @Singleton
    fun provideShikimoriUploader(
        @ApplicationContext context: Context,
        userApi: UserApi
    ): MediaUploader = ShikimoriUploader(context, userApi)
}