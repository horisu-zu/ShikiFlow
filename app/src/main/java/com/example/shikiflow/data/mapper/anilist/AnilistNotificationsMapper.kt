package com.example.shikiflow.data.mapper.anilist

import com.example.graphql.anilist.NotificationsQuery
import com.example.shikiflow.domain.model.notification.AiringNotification
import com.example.shikiflow.domain.model.notification.BaseNotification
import com.example.shikiflow.domain.model.notification.Notification
import com.example.shikiflow.domain.model.notification.NotificationType
import com.example.graphql.anilist.type.NotificationType as ALNotificationType
import com.example.shikiflow.presentation.screen.more.notifications.NotificationTypeGroup
import kotlin.time.Instant

object AnilistNotificationsMapper {
    fun NotificationTypeGroup.toAnilistTypes(): List<ALNotificationType> {
        return when (this) {
            NotificationTypeGroup.ALL -> emptyList()
            NotificationTypeGroup.AIRING -> listOf(ALNotificationType.AIRING)
            NotificationTypeGroup.ACTIVITY -> listOf(
                ALNotificationType.ACTIVITY_MESSAGE,
                ALNotificationType.ACTIVITY_MENTION,
                ALNotificationType.ACTIVITY_REPLY,
                ALNotificationType.ACTIVITY_REPLY_SUBSCRIBED,
                ALNotificationType.ACTIVITY_LIKE,
                ALNotificationType.ACTIVITY_REPLY_LIKE
            )
            NotificationTypeGroup.FORUM -> listOf(
                ALNotificationType.THREAD_COMMENT_MENTION,
                ALNotificationType.THREAD_COMMENT_REPLY,
                ALNotificationType.THREAD_SUBSCRIBED,
                ALNotificationType.THREAD_COMMENT_LIKE,
                ALNotificationType.THREAD_LIKE
            )
            NotificationTypeGroup.FOLLOWS -> listOf(ALNotificationType.FOLLOWING)
            NotificationTypeGroup.MEDIA -> listOf(
                ALNotificationType.RELATED_MEDIA_ADDITION,
                ALNotificationType.MEDIA_DATA_CHANGE,
                ALNotificationType.MEDIA_MERGE,
                ALNotificationType.MEDIA_DELETION,
            )
            NotificationTypeGroup.SUBMISSION -> listOf(
                ALNotificationType.MEDIA_SUBMISSION_UPDATE,
                ALNotificationType.STAFF_SUBMISSION_UPDATE,
                ALNotificationType.CHARACTER_SUBMISSION_UPDATE
            )
        }
    }

    //WIP, there's a lot to change
    fun NotificationsQuery.Notification.toDomainNotification(): Notification? {
        onAiringNotification?.aLAiringNotification?.let { notification ->
            return AiringNotification(
                id = notification.id,
                text = notification.media?.title?.userPreferred.orEmpty(),
                contentId = notification.animeId,
                imageUrl = notification.media?.coverImage?.large.orEmpty(),
                timestamp = notification.createdAt.toInstant(),
                episodeNumber = notification.episode,
            )
        }

        onFollowingNotification?.aLFollowingNotification?.let { notification ->
            return userNotification(
                id = notification.id,
                user = notification.user?.name,
                avatar = notification.user?.avatar?.large,
                createdAt = notification.createdAt,
                type = NotificationType.FOLLOWING,
                contentId = notification.userId,
            )
        }

        onActivityMessageNotification?.aLActivityMessageNotification?.let { notification ->
            return userNotification(
                id = notification.id,
                user = notification.user?.name,
                avatar = notification.user?.avatar?.large,
                createdAt = notification.createdAt,
                type = NotificationType.ACTIVITY_MESSAGE,
                contentId = notification.activityId,
                secondaryContentId = notification.userId,
            )
        }
        onActivityMentionNotification?.aLActivityMentionNotification?.let { notification ->
            return userNotification(
                id = notification.id,
                user = notification.user?.name,
                avatar = notification.user?.avatar?.large,
                createdAt = notification.createdAt,
                type = NotificationType.ACTIVITY_MENTION,
                contentId = notification.activityId,
                secondaryContentId = notification.userId,
            )
        }
        onActivityReplyNotification?.aLActivityReplyNotification?.let { notification ->
            return userNotification(
                id = notification.id,
                user = notification.user?.name,
                avatar = notification.user?.avatar?.large,
                createdAt = notification.createdAt,
                type = NotificationType.ACTIVITY_REPLY,
                contentId = notification.activityId,
                secondaryContentId = notification.userId,
            )
        }
        onActivityReplySubscribedNotification?.aLActivityReplySubscribedNotification?.let { notification ->
            return userNotification(
                id = notification.id,
                user = notification.user?.name,
                avatar = notification.user?.avatar?.large,
                createdAt = notification.createdAt,
                type = NotificationType.ACTIVITY_REPLY_SUBSCRIBED,
                contentId = notification.activityId,
                secondaryContentId = notification.userId,
            )
        }
        onActivityLikeNotification?.aLActivityLikeNotification?.let { notification ->
            return userNotification(
                id = notification.id,
                user = notification.user?.name,
                avatar = notification.user?.avatar?.large,
                createdAt = notification.createdAt,
                type = NotificationType.ACTIVITY_LIKE,
                contentId = notification.activityId,
                secondaryContentId = notification.userId,
            )
        }
        onActivityReplyLikeNotification?.aLActivityReplyLikeNotification?.let { notification ->
            return userNotification(
                id = notification.id,
                user = notification.user?.name,
                avatar = notification.user?.avatar?.large,
                createdAt = notification.createdAt,
                type = NotificationType.ACTIVITY_REPLY_LIKE,
                contentId = notification.activityId,
                secondaryContentId = notification.userId,
            )
        }

        onThreadCommentMentionNotification?.aLThreadCommentMentionNotification?.let { notification ->
            return userNotification(
                id = notification.id,
                user = notification.user?.name,
                avatar = notification.user?.avatar?.large,
                createdAt = notification.createdAt,
                type = NotificationType.THREAD_COMMENT_MENTION,
                contentId = notification.thread?.id,
                secondaryContentId = notification.commentId,
            )
        }
        onThreadCommentReplyNotification?.aLThreadCommentReplyNotification?.let { notification ->
            return userNotification(
                id = notification.id,
                user = notification.user?.name,
                avatar = notification.user?.avatar?.large,
                createdAt = notification.createdAt,
                type = NotificationType.THREAD_COMMENT_REPLY,
                contentId = notification.thread?.id,
                secondaryContentId = notification.commentId,
            )
        }
        onThreadCommentSubscribedNotification?.aLThreadCommentSubscribedNotification?.let { notification ->
            return userNotification(
                id = notification.id,
                user = notification.user?.name,
                avatar = notification.user?.avatar?.large,
                createdAt = notification.createdAt,
                type = NotificationType.THREAD_SUBSCRIBED,
                contentId = notification.thread?.id,
                secondaryContentId = notification.commentId,
            )
        }
        onThreadCommentLikeNotification?.aLThreadCommentLikeNotification?.let { notification ->
            return userNotification(
                id = notification.id,
                user = notification.user?.name,
                avatar = notification.user?.avatar?.large,
                createdAt = notification.createdAt,
                type = NotificationType.THREAD_COMMENT_LIKE,
                contentId = notification.thread?.id,
                secondaryContentId = notification.commentId,
            )
        }
        onThreadLikeNotification?.aLThreadLikeNotification?.let { notification ->
            return userNotification(
                id = notification.id,
                user = notification.user?.name,
                avatar = notification.user?.avatar?.large,
                createdAt = notification.createdAt,
                type = NotificationType.THREAD_LIKE,
                contentId = notification.threadId,
                secondaryContentId = notification.userId,
            )
        }

        onRelatedMediaAdditionNotification?.aLRelatedMediaAdditionNotification?.let { notification ->
            return mediaNotification(
                id = notification.id,
                text = notification.media?.title?.userPreferred,
                imageUrl = notification.media?.coverImage?.large,
                createdAt = notification.createdAt,
                type = NotificationType.RELATED_MEDIA_ADDITION,
                contentId = notification.mediaId,
            )
        }
        onMediaDataChangeNotification?.aLMediaDataChangeNotification?.let { notification ->
            return mediaNotification(
                id = notification.id,
                text = notification.media?.title?.userPreferred,
                imageUrl = notification.media?.coverImage?.large,
                createdAt = notification.createdAt,
                type = NotificationType.MEDIA_DATA_CHANGE,
                contentId = notification.mediaId,
            )
        }
        onMediaMergeNotification?.aLMediaMergeNotification?.let { notification ->
            return mediaNotification(
                id = notification.id,
                text = notification.media?.title?.userPreferred,
                imageUrl = notification.media?.coverImage?.large,
                createdAt = notification.createdAt,
                type = NotificationType.MEDIA_MERGE,
                contentId = notification.mediaId,
            )
        }
        onMediaDeletionNotification?.aLMediaDeletionNotification?.let { notification ->
            return mediaNotification(
                id = notification.id,
                text = notification.deletedMediaTitle,
                imageUrl = null,
                createdAt = notification.createdAt,
                type = NotificationType.MEDIA_DELETION,
                contentId = null,
            )
        }

        onMediaSubmissionUpdateNotification?.aLMediaSubmissionUpdateNotification?.let { notification ->
            return mediaNotification(
                id = notification.id,
                text = notification.contexts?.filterNotNull()?.joinToString("\n").orEmpty(),
                imageUrl = notification.media?.coverImage?.large,
                createdAt = notification.createdAt,
                type = NotificationType.MEDIA_SUBMISSION_UPDATE,
                contentId = notification.media?.id,
            )
        }
        onStaffSubmissionUpdateNotification?.aLStaffSubmissionUpdateNotification?.let { notification ->
            return mediaNotification(
                id = notification.id,
                text = notification.contexts?.filterNotNull()?.joinToString("\n").orEmpty(),
                imageUrl = notification.staff?.image?.large,
                createdAt = notification.createdAt,
                type = NotificationType.STAFF_SUBMISSION_UPDATE,
                contentId = notification.staff?.id,
            )
        }
        onCharacterSubmissionUpdateNotification?.aLCharacterSubmissionUpdateNotification?.let { notification ->
            return mediaNotification(
                id = notification.id,
                text = notification.contexts?.filterNotNull()?.joinToString("\n").orEmpty(),
                imageUrl = notification.character?.image?.large,
                createdAt = notification.createdAt,
                type = NotificationType.CHARACTER_SUBMISSION_UPDATE,
                contentId = notification.character?.id,
            )
        }

        return null
    }

    private fun userNotification(
        id: Int,
        user: String?,
        avatar: String?,
        createdAt: Int?,
        type: NotificationType,
        contentId: Int?,
        secondaryContentId: Int? = null,
    ) = BaseNotification(
        id = id,
        text = user.orEmpty(),
        contentId = contentId ?: 0,
        imageUrl = avatar.orEmpty(),
        timestamp = createdAt.toInstant(),
        type = type,
        secondaryContentId = secondaryContentId,
    )

    private fun mediaNotification(
        id: Int,
        text: String?,
        imageUrl: String?,
        createdAt: Int?,
        type: NotificationType,
        contentId: Int?,
    ) = BaseNotification(
        id = id,
        text = text.orEmpty(),
        contentId = contentId ?: 0,
        imageUrl = imageUrl.orEmpty(),
        timestamp = createdAt.toInstant(),
        type = type,
        secondaryContentId = null,
    )

    private fun Int?.toInstant() = Instant.fromEpochSeconds(this?.toLong() ?: 0L)
}