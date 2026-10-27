package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.notification.NotificationType

object NotificationMapper {
    fun NotificationType.displayValue(): Int {
        return when (this) {
            NotificationType.AIRING -> R.string.notification_type_airing_label
            NotificationType.FOLLOWING -> R.string.notification_type_following_label
            NotificationType.ACTIVITY_MESSAGE -> R.string.notification_type_activity_message_label
            NotificationType.ACTIVITY_MENTION -> R.string.notification_type_activity_mention_label
            NotificationType.ACTIVITY_REPLY -> R.string.notification_type_activity_reply_label
            NotificationType.ACTIVITY_REPLY_SUBSCRIBED -> R.string.notification_type_activity_reply_subscribed_label
            NotificationType.ACTIVITY_LIKE -> R.string.notification_type_activity_like_label
            NotificationType.ACTIVITY_REPLY_LIKE -> R.string.notification_type_activity_reply_like_label
            NotificationType.THREAD_COMMENT_MENTION -> R.string.notification_type_thread_comment_mention_label
            NotificationType.THREAD_COMMENT_REPLY -> R.string.notification_type_thread_comment_reply_label
            NotificationType.THREAD_SUBSCRIBED -> R.string.notification_type_thread_subscribed_label
            NotificationType.THREAD_COMMENT_LIKE -> R.string.notification_type_thread_comment_like_label
            NotificationType.THREAD_LIKE -> R.string.notification_type_thread_like_label
            NotificationType.RELATED_MEDIA_ADDITION -> R.string.notification_type_related_media_addition_label
            NotificationType.MEDIA_DATA_CHANGE -> R.string.notification_type_media_data_change_label
            NotificationType.MEDIA_MERGE -> R.string.notification_type_media_merge_label
            NotificationType.MEDIA_DELETION -> R.string.notification_type_media_deletion_label
            NotificationType.MEDIA_SUBMISSION_UPDATE -> R.string.notification_type_media_submission_update_label
            NotificationType.STAFF_SUBMISSION_UPDATE -> R.string.notification_type_staff_submission_update_label
            NotificationType.CHARACTER_SUBMISSION_UPDATE -> R.string.notification_type_character_submission_update_label
        }
    }

    fun NotificationType.description(): Int {
        return when (this) {
            NotificationType.AIRING -> R.string.notification_type_airing_description
            NotificationType.FOLLOWING -> R.string.notification_type_following_description
            NotificationType.ACTIVITY_MESSAGE -> R.string.notification_type_activity_message_description
            NotificationType.ACTIVITY_MENTION -> R.string.notification_type_activity_mention_description
            NotificationType.ACTIVITY_REPLY -> R.string.notification_type_activity_reply_description
            NotificationType.ACTIVITY_REPLY_SUBSCRIBED -> R.string.notification_type_activity_reply_subscribed_description
            NotificationType.ACTIVITY_LIKE -> R.string.notification_type_activity_like_description
            NotificationType.ACTIVITY_REPLY_LIKE -> R.string.notification_type_activity_reply_like_description
            NotificationType.THREAD_COMMENT_MENTION -> R.string.notification_type_thread_comment_mention_description
            NotificationType.THREAD_COMMENT_REPLY -> R.string.notification_type_thread_comment_reply_description
            NotificationType.THREAD_SUBSCRIBED -> R.string.notification_type_thread_subscribed_description
            NotificationType.THREAD_COMMENT_LIKE -> R.string.notification_type_thread_comment_like_description
            NotificationType.THREAD_LIKE -> R.string.notification_type_thread_like_description
            NotificationType.RELATED_MEDIA_ADDITION -> R.string.notification_type_related_media_addition_description
            NotificationType.MEDIA_DATA_CHANGE -> R.string.notification_type_media_data_change_description
            NotificationType.MEDIA_MERGE -> R.string.notification_type_media_merge_description
            NotificationType.MEDIA_DELETION -> R.string.notification_type_media_deletion_description
            NotificationType.MEDIA_SUBMISSION_UPDATE -> R.string.notification_type_media_submission_update_description
            NotificationType.STAFF_SUBMISSION_UPDATE -> R.string.notification_type_staff_submission_update_description
            NotificationType.CHARACTER_SUBMISSION_UPDATE -> R.string.notification_type_character_submission_update_description
        }
    }
}