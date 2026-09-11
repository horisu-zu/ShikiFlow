package com.example.shikiflow.worker.notification

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.apollographql.apollo.api.not
import com.example.shikiflow.domain.repository.SettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val handlers: Set<@JvmSuppressWildcards NotificationHandler>,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            val notificationSettings = settingsRepository.notificationSettingsFlow.first()

            handlers.forEach { handler ->
                when (handler) {
                    is AiringNotificationHandler -> if (notificationSettings.showAiringNotifications) {
                        handler.checkAndNotify()
                    }
                    is UpcomingNotificationHandler -> if (notificationSettings.showUpcomingNotifications) {
                        handler.checkAndNotify()
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("Notification Worker", "Notification Work failed", e)

            Result.retry()
        }
    }
}