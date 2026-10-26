package com.example.shikiflow.presentation.screen.more.notifications

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.notification.NotificationType

enum class NotificationTypeGroup(val types: List<NotificationType> = emptyList()) {
    ALL,
    AIRING(
        listOf(NotificationType.AIRING)
    ),
    ACTIVITY(
        listOf(
            NotificationType.ACTIVITY_MESSAGE,
            NotificationType.ACTIVITY_MENTION,
            NotificationType.ACTIVITY_REPLY,
            NotificationType.ACTIVITY_REPLY_SUBSCRIBED,
            NotificationType.ACTIVITY_LIKE,
            NotificationType.ACTIVITY_REPLY_LIKE
        )
    ),
    FORUM(
        listOf(
            NotificationType.THREAD_COMMENT_MENTION,
            NotificationType.THREAD_COMMENT_REPLY,
            NotificationType.THREAD_SUBSCRIBED,
            NotificationType.THREAD_COMMENT_LIKE,
            NotificationType.THREAD_LIKE
        )
    ),
    FOLLOWS(
        listOf(NotificationType.FOLLOWING)
    ),
    MEDIA(
        listOf(
            NotificationType.RELATED_MEDIA_ADDITION,
            NotificationType.MEDIA_DATA_CHANGE,
            NotificationType.MEDIA_MERGE,
            NotificationType.MEDIA_DELETION
        )
    ),
    SUBMISSION(
        listOf(
            NotificationType.MEDIA_SUBMISSION_UPDATE,
            NotificationType.STAFF_SUBMISSION_UPDATE,
            NotificationType.CHARACTER_SUBMISSION_UPDATE
        )
    );

    companion object {
        fun NotificationTypeGroup.displayValue(): Int {
            return when (this) {
                ALL -> R.string.common_all
                AIRING -> R.string.notification_type_group_airing
                ACTIVITY -> R.string.notification_type_group_activity
                FORUM -> R.string.notification_type_group_forum
                FOLLOWS -> R.string.notification_type_group_follows
                MEDIA -> R.string.notification_type_group_media
                SUBMISSION -> R.string.notification_type_group_submission
            }
        }
    }
}