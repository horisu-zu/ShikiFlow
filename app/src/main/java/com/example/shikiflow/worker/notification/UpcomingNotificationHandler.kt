package com.example.shikiflow.worker.notification

import android.content.Context
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.episode_notification.EpisodeNotification
import com.example.shikiflow.domain.model.episode_notification.EpisodeNotificationType
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.EpisodeNotificationRepository
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.DateUtils.timeDifference
import com.example.shikiflow.utils.notifications.NotificationUtils.showNotification
import com.example.shikiflow.worker.notification.AiringNotificationHandler.Companion.AIRING_GROUP_ID
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

class UpcomingNotificationHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaTracksRepository: MediaTracksRepository,
    private val settingsRepository: SettingsRepository,
    private val episodeNotificationRepository: EpisodeNotificationRepository
): NotificationHandler {

    companion object {
        const val UPCOMING_CHANNEL_ID = "upcoming_channel"
    }

    override suspend fun checkAndNotify() {
        val resources = context.resources
        val titleType = settingsRepository.userSettingsFlow.firstOrNull()
            ?.preferredTitleType ?: PreferredTitleType.ROMAJI

        val airingSoonMedia =  mediaTracksRepository.getLocalMediaTracks(MediaType.ANIME)
            .map { mediaTrack -> mediaTrack.shortData }
            .filter { mediaTrack ->
                mediaTrack.currentProgress == 0 && mediaTrack.nextEpisodeAt != null &&
                mediaTrack.nextEpisodeAt.timeDifference() in 0.seconds..1.days
            }
            .filter { mediaTrack ->
                !episodeNotificationRepository.checkNotified(
                    mediaId = mediaTrack.id,
                    episode = mediaTrack.currentProgress?.plus(1) ?: 1,
                    type = EpisodeNotificationType.UPCOMING
                )
            }

        if (airingSoonMedia.size > 3) {
            context.showNotification(
                notificationId = 0,
                channelId = UPCOMING_CHANNEL_ID,
                group = AIRING_GROUP_ID,
                title = resources.getString(R.string.airing_premiere_summary_notification_label),
                text = airingSoonMedia.joinToString(", ") { media ->
                    media.title.preferred(titleType)
                },
                isGroupSummary = true
            )
        }

        airingSoonMedia.forEach { media ->
            runCatching {
                context.showNotification(
                    notificationId = media.id,
                    channelId = UPCOMING_CHANNEL_ID,
                    group = AIRING_GROUP_ID,
                    title = media.title.preferred(titleType),
                    text = resources.getString(R.string.airing_premiere_notification_label)
                )
            }.onSuccess {
                episodeNotificationRepository.saveEntry(
                    EpisodeNotification(
                        mediaId = media.id,
                        episode = media.currentProgress?.plus(1) ?: 1,
                        type = EpisodeNotificationType.UPCOMING
                    )
                )
            }
        }
    }
}