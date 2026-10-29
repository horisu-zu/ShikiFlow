package com.example.shikiflow.worker.notification

import android.content.Context
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.episode_notification.EpisodeNotification
import com.example.shikiflow.domain.model.episode_notification.EpisodeNotificationType
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.EpisodeNotificationRepository
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.presentation.common.mappers.DateMapper.untilPremiere
import com.example.shikiflow.utils.DateUtils
import com.example.shikiflow.utils.DateUtils.timeDifference
import com.example.shikiflow.utils.notifications.NotificationUtils.showNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
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
        const val UPCOMING_GROUP_ID = "upcoming_group"
    }

    override suspend fun checkAndNotify() {
        val deliveryWindow = settingsRepository.notificationSettingsFlow
            .map { it.timeRange }
            .first()

        if (!DateUtils.isWithinWindow(deliveryWindow)) {
            return
        }

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
                    episode = 1,
                    type = EpisodeNotificationType.UPCOMING
                )
            }

        if (airingSoonMedia.size > 1) {
            context.showNotification(
                notificationId = 0,
                channelId = UPCOMING_CHANNEL_ID,
                group = UPCOMING_GROUP_ID,
                title = resources.getString(R.string.airing_premiere_summary_notification_label),
                text = airingSoonMedia.joinToString(", ") { media ->
                    media.title.preferred(titleType)
                },
                isGroupSummary = true
            )
        }

        airingSoonMedia.forEach { media ->
            context.showNotification(
                notificationId = media.id,
                channelId = UPCOMING_CHANNEL_ID,
                group = UPCOMING_GROUP_ID,
                title = media.title.preferred(titleType),
                text = buildString {
                    when (media.kind) {
                        MediaFormat.TV -> append(resources.getString(R.string.airing_premiere_tv_notification_label))
                        else -> append(resources.getString(R.string.airing_premiere_notification_label))
                    }
                    append(" ")
                    append(media.nextEpisodeAt!!.untilPremiere(resources))
                }
            ).also { notified ->
                if (notified) {
                    episodeNotificationRepository.saveEntry(
                        EpisodeNotification(
                            mediaId = media.id,
                            episode = 1,
                            type = EpisodeNotificationType.UPCOMING
                        )
                    )
                }
            }
        }
    }
}