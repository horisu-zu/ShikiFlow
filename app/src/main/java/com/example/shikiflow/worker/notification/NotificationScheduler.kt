package com.example.shikiflow.worker.notification

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    companion object {
        private const val NOTIFICATION_WORK_NAME = "NotificationWork"
        private const val BACKOFF_DELAY = 60 * 1000L
    }

    private val workManager by lazy { WorkManager.getInstance(context) }

    fun schedulePeriodicWork() {
        val airingMediaRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            repeatInterval = 15, repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BACKOFF_DELAY, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName = NOTIFICATION_WORK_NAME,
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
            request = airingMediaRequest
        )
    }

    fun cancel() {
        workManager.cancelUniqueWork(NOTIFICATION_WORK_NAME)
    }
}