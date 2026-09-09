package com.example.shikiflow.data.repository

import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.mapper.common.EpisodeNotificationMapper.toEntity
import com.example.shikiflow.domain.model.episode_notification.EpisodeNotification
import com.example.shikiflow.domain.model.episode_notification.EpisodeNotificationType
import com.example.shikiflow.domain.repository.EpisodeNotificationRepository
import javax.inject.Inject
import kotlin.time.Instant

class EpisodeNotificationRepositoryImpl @Inject constructor(
    private val appRoomDatabase: AppRoomDatabase
): EpisodeNotificationRepository {

    private val notificationDao = appRoomDatabase.episodeNotificationsDao()

    override suspend fun saveEntry(episodeNotification: EpisodeNotification) {
        notificationDao.insert(episodeNotification.toEntity())
    }

    override suspend fun checkNotified(
        mediaId: Int,
        episode: Int,
        type: EpisodeNotificationType
    ): Boolean = notificationDao.notified(mediaId, episode, type)

    override suspend fun deleteOlderThan(instant: Instant) {
        notificationDao.deleteOlderThan(instant)
    }
}