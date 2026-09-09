package com.example.shikiflow.worker.notification

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.DateUtils.timeDifference
import com.example.shikiflow.utils.notifications.NotificationUtils.showNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val handlers: Set<@JvmSuppressWildcards NotificationHandler>
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            handlers.forEach { handler ->
                handler.checkAndNotify()
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("Notification Worker", "Notification Work failed", e)

            Result.retry()
        }
    }
}