package com.example.shikiflow.worker.notification

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.shikiflow.domain.model.episode_notification.EpisodeNotificationType
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.EpisodeNotificationRepository
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.DateUtils.timeDifference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class AiringNotificationHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaTracksRepository: MediaTracksRepository,
    private val settingsRepository: SettingsRepository,
    private val episodeNotificationRepository: EpisodeNotificationRepository
): NotificationHandler {

    companion object {
        const val AIRING_CHANNEL_ID = "airing_channel"
        const val AIRING_GROUP_ID = "airing_group"
    }

    override suspend fun checkAndNotify() {
        val titleType = settingsRepository.userSettingsFlow.firstOrNull()
            ?.preferredTitleType ?: PreferredTitleType.ROMAJI

        val airingMedia =  mediaTracksRepository.getLocalMediaTracks(MediaType.ANIME)
            .map { mediaTrack -> mediaTrack.shortData }
            .filter { mediaTrack ->
                mediaTrack.nextEpisodeAt != null &&
                mediaTrack.nextEpisodeAt.timeDifference() in 0.seconds..30.minutes
            }
            .filter { mediaTrack ->
                !episodeNotificationRepository.checkNotified(
                    mediaId = mediaTrack.id,
                    episode = mediaTrack.currentProgress?.plus(1) ?: 1,
                    type = EpisodeNotificationType.AIRING
                )
            }

        Log.d("AiringNotificationHandler", "Airing Media: $airingMedia")

        val now = Clock.System.now()
        val airingDelayMs = settingsRepository.notificationSettingsFlow
            .map { notificationSettings -> notificationSettings.airingDelay }
            .first()
            .roundToInt() * 60L * 1000L

        airingMedia.forEach { media ->
            val episode = media.currentProgress?.plus(1) ?: 1
            val delayMs = media.nextEpisodeAt!!.minus(now)
                .inWholeMilliseconds
                .coerceAtLeast(0L) + airingDelayMs

            Log.d("AiringNotificationHandler", "Showing notification in ${delayMs / 1000L / 60L} mins")

            val inputData = workDataOf(
                AiringNotificationWorker.KEY_MEDIA_ID to media.id,
                AiringNotificationWorker.KEY_EPISODE to episode,
                AiringNotificationWorker.KEY_TITLE to media.title.preferred(titleType)
            )

            val request = OneTimeWorkRequestBuilder<AiringNotificationWorker>()
                .setInitialDelay(delayMs, timeUnit = TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 15 * 1000L, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                uniqueWorkName = "airing_episode_notification_${media.id}_$episode",
                existingWorkPolicy = ExistingWorkPolicy.KEEP,
                request = request
            )
        }

        episodeNotificationRepository.deleteOlderThan(Clock.System.now() - 7.days)
    }
}