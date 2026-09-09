package com.example.shikiflow.domain.model.episode_notification

import kotlin.time.Clock
import kotlin.time.Instant

data class EpisodeNotification(
    val mediaId: Int,
    val episode: Int,
    val notifiedAt: Instant = Clock.System.now(),
    val type: EpisodeNotificationType
)
