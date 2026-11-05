package com.example.shikiflow.data.mapper.anilist

import com.example.graphql.anilist.NotificationsQuery
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toDomainUser
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toNotificationActivity
import com.example.shikiflow.data.mapper.common.MediaTitleMapper.toDomainTitle
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toDomain
import com.example.shikiflow.domain.model.notification.ActivityNotification
import com.example.shikiflow.domain.model.notification.AiringNotification
import com.example.shikiflow.domain.model.notification.FollowingNotification
import com.example.shikiflow.domain.model.notification.MediaDeletionNotification
import com.example.shikiflow.domain.model.notification.MediaMergeNotification
import com.example.shikiflow.domain.model.notification.MediaNotification
import com.example.shikiflow.domain.model.notification.Notification
import com.example.shikiflow.domain.model.notification.NotificationType
import com.example.shikiflow.domain.model.notification.SubmissionNotification
import com.example.shikiflow.domain.model.notification.SubmissionStatus
import com.example.shikiflow.domain.model.notification.ThreadCommentNotification
import com.example.shikiflow.domain.model.notification.ThreadNotification
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.activity.ActivityType
import com.example.shikiflow.domain.model.user.activity.TextNotificationActivity
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

    fun ALNotificationType.toDomainType(): NotificationType {
        return when (this) {
            ALNotificationType.ACTIVITY_MESSAGE -> NotificationType.ACTIVITY_MESSAGE
            ALNotificationType.ACTIVITY_REPLY -> NotificationType.ACTIVITY_REPLY
            ALNotificationType.FOLLOWING -> NotificationType.FOLLOWING
            ALNotificationType.ACTIVITY_MENTION -> NotificationType.ACTIVITY_MENTION
            ALNotificationType.THREAD_COMMENT_MENTION -> NotificationType.THREAD_COMMENT_MENTION
            ALNotificationType.THREAD_SUBSCRIBED -> NotificationType.THREAD_SUBSCRIBED
            ALNotificationType.THREAD_COMMENT_REPLY -> NotificationType.THREAD_COMMENT_REPLY
            ALNotificationType.ACTIVITY_LIKE -> NotificationType.ACTIVITY_LIKE
            ALNotificationType.ACTIVITY_REPLY_LIKE -> NotificationType.ACTIVITY_REPLY_LIKE
            ALNotificationType.THREAD_LIKE -> NotificationType.THREAD_LIKE
            ALNotificationType.THREAD_COMMENT_LIKE -> NotificationType.THREAD_COMMENT_LIKE
            ALNotificationType.ACTIVITY_REPLY_SUBSCRIBED -> NotificationType.ACTIVITY_REPLY_SUBSCRIBED
            ALNotificationType.RELATED_MEDIA_ADDITION -> NotificationType.RELATED_MEDIA_ADDITION
            ALNotificationType.MEDIA_DATA_CHANGE -> NotificationType.MEDIA_DATA_CHANGE
            ALNotificationType.MEDIA_MERGE -> NotificationType.MEDIA_MERGE
            ALNotificationType.MEDIA_DELETION -> NotificationType.MEDIA_DELETION
            ALNotificationType.MEDIA_SUBMISSION_UPDATE -> NotificationType.MEDIA_SUBMISSION_UPDATE
            ALNotificationType.STAFF_SUBMISSION_UPDATE -> NotificationType.STAFF_SUBMISSION_UPDATE
            ALNotificationType.CHARACTER_SUBMISSION_UPDATE -> NotificationType.CHARACTER_SUBMISSION_UPDATE
            else -> NotificationType.AIRING
        }
    }

    fun NotificationType.toAnilistType(): ALNotificationType {
        return when (this) {
            NotificationType.ACTIVITY_MESSAGE -> ALNotificationType.ACTIVITY_MESSAGE
            NotificationType.ACTIVITY_REPLY -> ALNotificationType.ACTIVITY_REPLY
            NotificationType.FOLLOWING -> ALNotificationType.FOLLOWING
            NotificationType.ACTIVITY_MENTION -> ALNotificationType.ACTIVITY_MENTION
            NotificationType.THREAD_COMMENT_MENTION -> ALNotificationType.THREAD_COMMENT_MENTION
            NotificationType.THREAD_SUBSCRIBED -> ALNotificationType.THREAD_SUBSCRIBED
            NotificationType.THREAD_COMMENT_REPLY -> ALNotificationType.THREAD_COMMENT_REPLY
            NotificationType.ACTIVITY_LIKE -> ALNotificationType.ACTIVITY_LIKE
            NotificationType.ACTIVITY_REPLY_LIKE -> ALNotificationType.ACTIVITY_REPLY_LIKE
            NotificationType.THREAD_LIKE -> ALNotificationType.THREAD_LIKE
            NotificationType.THREAD_COMMENT_LIKE -> ALNotificationType.THREAD_COMMENT_LIKE
            NotificationType.ACTIVITY_REPLY_SUBSCRIBED -> ALNotificationType.ACTIVITY_REPLY_SUBSCRIBED
            NotificationType.RELATED_MEDIA_ADDITION -> ALNotificationType.RELATED_MEDIA_ADDITION
            NotificationType.MEDIA_DATA_CHANGE -> ALNotificationType.MEDIA_DATA_CHANGE
            NotificationType.MEDIA_MERGE -> ALNotificationType.MEDIA_MERGE
            NotificationType.MEDIA_DELETION -> ALNotificationType.MEDIA_DELETION
            NotificationType.MEDIA_SUBMISSION_UPDATE -> ALNotificationType.MEDIA_SUBMISSION_UPDATE
            NotificationType.STAFF_SUBMISSION_UPDATE -> ALNotificationType.STAFF_SUBMISSION_UPDATE
            NotificationType.CHARACTER_SUBMISSION_UPDATE -> ALNotificationType.CHARACTER_SUBMISSION_UPDATE
            else -> ALNotificationType.AIRING
        }
    }

    //WIP, there's a lot to change
    fun NotificationsQuery.Notification.toDomainNotification(): Notification? {
        onAiringNotification?.aLAiringNotification?.let { notification ->
            return AiringNotification(
                id = notification.id,
                mediaTitle = notification.media?.title?.mediaTitle.toDomainTitle(),
                mediaId = notification.animeId,
                coverImageUrl = notification.media?.coverImage?.large.orEmpty(),
                createdAt = notification.createdAt.toInstant(),
                episodeNumber = notification.episode
            )
        }

        onFollowingNotification?.aLFollowingNotification?.let { notification ->
            notification.user?.aLUserShort?.let {
                return FollowingNotification(
                    id = notification.id,
                    createdAt = notification.createdAt.toInstant(),
                    type = NotificationType.FOLLOWING,
                    user = notification.user.aLUserShort.toDomainUser()
                )
            }
        }

        onActivityMessageNotification?.aLActivityMessageNotification?.let { notification ->
            notification.message?.let { messageActivity ->
                return ActivityNotification(
                    id = notification.id,
                    createdAt = notification.createdAt.toInstant(),
                    type = NotificationType.ACTIVITY_MESSAGE,
                    user = notification.user?.aLUserShort?.toDomainUser(),
                    activity = TextNotificationActivity(
                        id = messageActivity.id,
                        type = ActivityType.MESSAGE,
                        text = messageActivity.message.orEmpty(),
                        recipient = messageActivity.recipient?.aLUserShort?.toDomainUser()
                    )
                )
            }
        }
        onActivityMentionNotification?.aLActivityMentionNotification?.let { notification ->
            return ActivityNotification(
                id = notification.id,
                createdAt = notification.createdAt.toInstant(),
                type = NotificationType.ACTIVITY_MENTION,
                user = notification.user?.aLUserShort?.toDomainUser(),
                activity = notification.activity?.aLNotificationActivity?.toNotificationActivity()
            )
        }
        onActivityReplyNotification?.aLActivityReplyNotification?.let { notification ->
            return ActivityNotification(
                id = notification.id,
                createdAt = notification.createdAt.toInstant(),
                type = NotificationType.ACTIVITY_REPLY,
                user = notification.user?.aLUserShort?.toDomainUser(),
                activity = notification.activity?.aLNotificationActivity?.toNotificationActivity()
            )
        }
        onActivityReplySubscribedNotification?.aLActivityReplySubscribedNotification?.let { notification ->
            return ActivityNotification(
                id = notification.id,
                createdAt = notification.createdAt.toInstant(),
                type = NotificationType.ACTIVITY_REPLY_SUBSCRIBED,
                user = notification.user?.aLUserShort?.toDomainUser(),
                activity = notification.activity?.aLNotificationActivity?.toNotificationActivity()
            )
        }
        onActivityLikeNotification?.aLActivityLikeNotification?.let { notification ->
            return ActivityNotification(
                id = notification.id,
                createdAt = notification.createdAt.toInstant(),
                type = NotificationType.ACTIVITY_LIKE,
                user = notification.user?.aLUserShort?.toDomainUser(),
                activity = notification.activity?.aLNotificationActivity?.toNotificationActivity()
            )
        }
        onActivityReplyLikeNotification?.aLActivityReplyLikeNotification?.let { notification ->
            return ActivityNotification(
                id = notification.id,
                createdAt = notification.createdAt.toInstant(),
                type = NotificationType.ACTIVITY_REPLY_LIKE,
                user = notification.user?.aLUserShort?.toDomainUser(),
                activity = notification.activity?.aLNotificationActivity?.toNotificationActivity()
            )
        }

        onThreadCommentMentionNotification?.aLThreadCommentMentionNotification?.let { notification ->
            return ThreadCommentNotification(
                id = notification.id,
                createdAt = notification.createdAt.toInstant(),
                type = NotificationType.THREAD_COMMENT_MENTION,
                user = notification.user?.aLUserShort?.toDomainUser(),
                threadId = notification.thread?.id ?: 0,
                threadTitle = notification.thread?.title,
                commentId = notification.commentId,
                commentText = notification.comment?.comment.orEmpty()
            )
        }
        onThreadCommentReplyNotification?.aLThreadCommentReplyNotification?.let { notification ->
            return ThreadCommentNotification(
                id = notification.id,
                createdAt = notification.createdAt.toInstant(),
                type = NotificationType.THREAD_COMMENT_REPLY,
                user = notification.user?.aLUserShort?.toDomainUser(),
                threadId = notification.thread?.id ?: 0,
                threadTitle = notification.thread?.title,
                commentId = notification.commentId,
                commentText = notification.comment?.comment.orEmpty()
            )
        }
        onThreadCommentSubscribedNotification?.aLThreadCommentSubscribedNotification?.let { notification ->
            return ThreadCommentNotification(
                id = notification.id,
                createdAt = notification.createdAt.toInstant(),
                type = NotificationType.THREAD_SUBSCRIBED,
                user = notification.user?.aLUserShort?.toDomainUser(),
                threadId = notification.thread?.id ?: 0,
                threadTitle = notification.thread?.title,
                commentId = notification.commentId,
                commentText = notification.comment?.comment.orEmpty()
            )
        }
        onThreadCommentLikeNotification?.aLThreadCommentLikeNotification?.let { notification ->
            return ThreadCommentNotification(
                id = notification.id,
                createdAt = notification.createdAt.toInstant(),
                type = NotificationType.THREAD_COMMENT_LIKE,
                user = notification.user?.aLUserShort?.toDomainUser(),
                threadId = notification.thread?.id ?: 0,
                threadTitle = notification.thread?.title,
                commentId = notification.commentId,
                commentText = notification.comment?.comment.orEmpty()
            )
        }
        onThreadLikeNotification?.aLThreadLikeNotification?.let { notification ->
            return ThreadNotification(
                id = notification.id,
                createdAt = notification.createdAt.toInstant(),
                type = NotificationType.THREAD_LIKE,
                user = notification.user?.aLUserShort?.toDomainUser(),
                threadId = notification.thread?.id ?: 0,
                threadTitle = notification.thread?.title
            )
        }

        onRelatedMediaAdditionNotification?.aLRelatedMediaAdditionNotification?.let { notification ->
            return MediaNotification(
                id = notification.id,
                mediaTitle = notification.media?.title?.mediaTitle.toDomainTitle(),
                mediaId = notification.mediaId,
                coverImageUrl = notification.media?.coverImage?.large,
                createdAt = notification.createdAt.toInstant(),
                type = NotificationType.RELATED_MEDIA_ADDITION,
                mediaType = notification.media?.type?.toDomain() ?: MediaType.ANIME,
                reason = null
            )
        }
        onMediaDataChangeNotification?.aLMediaDataChangeNotification?.let { notification ->
            return MediaNotification(
                id = notification.id,
                mediaTitle = notification.media?.title?.mediaTitle.toDomainTitle(),
                mediaId = notification.mediaId,
                coverImageUrl = notification.media?.coverImage?.large,
                createdAt = notification.createdAt.toInstant(),
                type = NotificationType.MEDIA_DATA_CHANGE,
                mediaType = notification.media?.type?.toDomain() ?: MediaType.ANIME,
                reason = notification.reason
            )
        }
        onMediaMergeNotification?.aLMediaMergeNotification?.let { notification ->
            return MediaMergeNotification(
                id = notification.id,
                mediaTitle = notification.media?.title?.mediaTitle.toDomainTitle(),
                mediaId = notification.mediaId,
                coverImageUrl = notification.media?.coverImage?.large,
                createdAt = notification.createdAt.toInstant(),
                mediaType = notification.media?.type?.toDomain() ?: MediaType.ANIME,
                mergedMediaTitles = notification.deletedMediaTitles?.filterNotNull() ?: emptyList(),
                reason = notification.reason
            )
        }
        onMediaDeletionNotification?.aLMediaDeletionNotification?.let { notification ->
            return MediaDeletionNotification(
                id = notification.id,
                deletedMediaTitle = notification.deletedMediaTitle.orEmpty(),
                createdAt = notification.createdAt.toInstant()
            )
        }

        onMediaSubmissionUpdateNotification?.aLMediaSubmissionUpdateNotification?.let { notification ->
            return SubmissionNotification(
                id = notification.id,
                createdAt = notification.createdAt.toInstant(),
                type = NotificationType.MEDIA_SUBMISSION_UPDATE,
                text = notification.media?.title?.userPreferred.orEmpty(),
                imageUrl = notification.media?.coverImage?.large,
                submissionStatus = notification.status.toDomainStatus(),
                notes = notification.notes,
                contentId = notification.media?.id ?: 0,
                mediaType = notification.media?.type?.toDomain()
            )
        }
        onStaffSubmissionUpdateNotification?.aLStaffSubmissionUpdateNotification?.let { notification ->
            return SubmissionNotification(
                id = notification.id,
                createdAt = notification.createdAt.toInstant(),
                type = NotificationType.STAFF_SUBMISSION_UPDATE,
                text = notification.staff?.name?.userPreferred.orEmpty(),
                imageUrl = notification.staff?.image?.large,
                submissionStatus = notification.status.toDomainStatus(),
                notes = notification.notes,
                contentId = notification.staff?.id ?: 0
            )
        }
        onCharacterSubmissionUpdateNotification?.aLCharacterSubmissionUpdateNotification?.let { notification ->
            return SubmissionNotification(
                id = notification.id,
                createdAt = notification.createdAt.toInstant(),
                type = NotificationType.CHARACTER_SUBMISSION_UPDATE,
                text = notification.character?.name?.userPreferred.orEmpty(),
                imageUrl = notification.character?.image?.large,
                submissionStatus = notification.status.toDomainStatus(),
                notes = notification.notes,
                contentId = notification.character?.id ?: 0
            )
        }

        return null
    }

    private fun String?.toDomainStatus(): SubmissionStatus {
        return when (this?.lowercase()) {
            "accepted" -> SubmissionStatus.ACCEPTED
            "rejected" -> SubmissionStatus.REJECTED
            else -> SubmissionStatus.PARTIALLY_ACCEPTED
        }
    }

    private fun Int?.toInstant() = Instant.fromEpochSeconds(this?.toLong() ?: 0L)
}