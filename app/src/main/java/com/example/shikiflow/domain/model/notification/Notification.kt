package com.example.shikiflow.domain.model.notification

import kotlin.time.Instant

sealed interface Notification {
    val id: Int
    val text: String
    val contentId: Int
    val imageUrl: String?
    val timestamp: Instant
    val type: NotificationType
}

data class BaseNotification(
    override val id: Int,
    override val text: String,
    override val contentId: Int,
    override val imageUrl: String,
    override val timestamp: Instant,
    override val type: NotificationType,
    val secondaryContentId: Int?
): Notification

data class AiringNotification(
    override val id: Int,
    override val text: String,
    override val contentId: Int,
    override val imageUrl: String,
    override val type: NotificationType = NotificationType.AIRING,
    override val timestamp: Instant,
    val episodeNumber: Int
): Notification