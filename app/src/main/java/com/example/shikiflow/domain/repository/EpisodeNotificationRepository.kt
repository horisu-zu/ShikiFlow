package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.episode_notification.EpisodeNotification
import com.example.shikiflow.domain.model.episode_notification.EpisodeNotificationType
import kotlin.time.Instant

interface EpisodeNotificationRepository {
    suspend fun saveEntry(episodeNotification: EpisodeNotification)

    suspend fun checkNotified(mediaId: Int, episode: Int, type: EpisodeNotificationType): Boolean

    suspend fun deleteOlderThan(instant: Instant)
}