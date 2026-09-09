package com.example.shikiflow.data.mapper.common

import com.example.shikiflow.data.local.entity.episode.EpisodeNotificationEntity
import com.example.shikiflow.domain.model.episode_notification.EpisodeNotification

object EpisodeNotificationMapper {
    fun EpisodeNotification.toEntity(): EpisodeNotificationEntity {
        return EpisodeNotificationEntity(
            mediaId = mediaId,
            episode = episode,
            notifiedAt = notifiedAt,
            type = type
        )
    }
}