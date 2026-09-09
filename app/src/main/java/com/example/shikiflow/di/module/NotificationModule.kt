package com.example.shikiflow.di.module

import com.example.shikiflow.worker.notification.AiringNotificationHandler
import com.example.shikiflow.worker.notification.NotificationHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
interface NotificationModule {

    @Binds
    @IntoSet
    fun bindAiringHandler(airingHandler: AiringNotificationHandler): NotificationHandler
}