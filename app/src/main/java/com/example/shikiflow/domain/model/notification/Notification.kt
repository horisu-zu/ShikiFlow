package com.example.shikiflow.domain.model.notification

import com.example.shikiflow.domain.model.media_details.MediaTitle
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.activity.NotificationActivity
import kotlin.time.Instant

sealed interface Notification {
    val id: Int
    val createdAt: Instant
    val type: NotificationType
}

data class AiringNotification(
    override val id: Int,
    override val type: NotificationType = NotificationType.AIRING,
    override val createdAt: Instant,
    val mediaTitle: MediaTitle,
    val mediaId: Int,
    val coverImageUrl: String?,
    val episodeNumber: Int
): Notification

data class FollowingNotification(
    override val id: Int,
    override val createdAt: Instant,
    override val type: NotificationType,
    val user: User
): Notification

data class MediaNotification(
    override val id: Int,
    override val createdAt: Instant,
    override val type: NotificationType,
    val mediaTitle: MediaTitle,
    val mediaId: Int,
    val coverImageUrl: String?,
    val mediaType: MediaType,
    val reason: String?
): Notification

data class MediaMergeNotification(
    override val id: Int,
    override val createdAt: Instant,
    override val type: NotificationType = NotificationType.MEDIA_MERGE,
    val mediaTitle: MediaTitle,
    val mediaId: Int,
    val coverImageUrl: String?,
    val mediaType: MediaType,
    val mergedMediaTitles: List<String>,
    val reason: String?
): Notification

data class MediaDeletionNotification(
    override val id: Int,
    override val createdAt: Instant,
    override val type: NotificationType = NotificationType.MEDIA_DELETION,
    val deletedMediaTitle: String
): Notification

data class ActivityNotification(
    override val id: Int,
    override val createdAt: Instant,
    override val type: NotificationType,
    val user: User?,
    val activity: NotificationActivity?
): Notification

data class ThreadNotification(
    override val id: Int,
    override val createdAt: Instant,
    override val type: NotificationType,
    val user: User?,
    val threadId: Int,
    val threadTitle: String?
): Notification

data class ThreadCommentNotification(
    override val id: Int,
    override val createdAt: Instant,
    override val type: NotificationType,
    val user: User?,
    val threadId: Int,
    val threadTitle: String?,
    val commentId: Int,
    val commentText: String
): Notification

data class SubmissionNotification(
    override val id: Int,
    override val createdAt: Instant,
    override val type: NotificationType,
    val imageUrl: String?,
    val text: String,
    val submissionStatus: SubmissionStatus,
    val notes: String?,
    val contentId: Int,
    val mediaType: MediaType? = null
): Notification