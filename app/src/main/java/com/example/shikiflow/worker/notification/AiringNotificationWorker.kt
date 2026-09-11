package com.example.shikiflow.worker.notification

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.episode_notification.EpisodeNotification
import com.example.shikiflow.domain.model.episode_notification.EpisodeNotificationType
import com.example.shikiflow.domain.repository.EpisodeNotificationRepository
import com.example.shikiflow.utils.notifications.NotificationUtils.showNotification
import com.example.shikiflow.worker.notification.AiringNotificationHandler.Companion.AIRING_CHANNEL_ID
import com.example.shikiflow.worker.notification.AiringNotificationHandler.Companion.AIRING_GROUP_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AiringNotificationWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val episodeNotificationRepository: EpisodeNotificationRepository
): CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_MEDIA_ID = "media_id"
        const val KEY_EPISODE = "episode"
        const val KEY_TITLE = "title"
    }

    override suspend fun doWork(): Result {
        return try {
            val resources = appContext.resources

            val mediaId = inputData.getInt(KEY_MEDIA_ID, -1)
            val episode = inputData.getInt(KEY_EPISODE, -1)
            val title = inputData.getString(KEY_TITLE) ?: ""

            appContext.showNotification(
                notificationId = mediaId,
                channelId = AIRING_CHANNEL_ID,
                group = AIRING_GROUP_ID,
                title = title,
                text = resources.getString(R.string.airing_episode_notification_label, episode)
            )

            episodeNotificationRepository.saveEntry(
                EpisodeNotification(
                    mediaId = mediaId,
                    episode = episode,
                    type = EpisodeNotificationType.AIRING
                )
            )

            Result.success()
        } catch (e: Exception) {
            Log.e("AiringNotificationWorker", "Failed to show notification", e)

            Result.retry()
        }
    }
}