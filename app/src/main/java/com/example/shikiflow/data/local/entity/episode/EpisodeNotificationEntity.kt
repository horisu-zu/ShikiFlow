package com.example.shikiflow.data.local.entity.episode

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.shikiflow.domain.model.episode_notification.EpisodeNotificationType
import kotlin.time.Clock
import kotlin.time.Instant

@Entity(
    tableName = "episode_notifications",
    primaryKeys = ["mediaId", "episode"]
)
data class EpisodeNotificationEntity(
    val mediaId: Int,
    val episode: Int,
    val notifiedAt: Instant = Clock.System.now(),
    @ColumnInfo(defaultValue = "AIRING") val type: EpisodeNotificationType
)