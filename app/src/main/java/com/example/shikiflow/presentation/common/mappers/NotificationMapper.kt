package com.example.shikiflow.presentation.common.mappers

import android.content.res.Resources
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
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
import com.example.shikiflow.presentation.screen.main.LocalTitleTypeController
import com.example.shikiflow.utils.StringUtils.joinWithLast

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

    fun SubmissionStatus.displayValue(): Int {
        return when (this) {
            SubmissionStatus.ACCEPTED -> R.string.submission_status_accepted
            SubmissionStatus.PARTIALLY_ACCEPTED -> R.string.submission_status_partially_accepted
            SubmissionStatus.REJECTED -> R.string.submission_status_rejected
        }
    }

    fun Notification.annotatedText(
        resources: Resources,
        titleType: PreferredTitleType,
        linkStyle: SpanStyle = SpanStyle(),
        onEntityClick: (EntityType, Int) -> Unit = { _, _ -> }
    ): AnnotatedString {
        val notification = this

        return buildAnnotatedString {
            when (notification) {
                is AiringNotification -> {
                    appendTemplate(
                        template = resources.getString((R.string.notification_type_airing_text)),
                        { append(notification.episodeNumber.toString()) },
                        { link(notification.mediaTitle.preferred(titleType), linkStyle) { onEntityClick(EntityType.Anime, notification.mediaId) } }
                    )
                }
                is FollowingNotification -> {
                    appendTemplate(
                        template = resources.getString((R.string.notification_type_following_text)),
                        { link(notification.user.nickname, linkStyle) {
                            onEntityClick(EntityType.User(notification.user), notification.user.id)
                        } }
                    )
                }
                is ActivityNotification -> {
                    when (type) {
                        NotificationType.ACTIVITY_MESSAGE -> {
                            appendTemplate(
                                template = resources.getString((R.string.notification_type_activity_message_text)),
                                { link(notification.user?.nickname ?: "", linkStyle) {
                                    user?.let {
                                        onEntityClick(EntityType.User(user = user), user.id)
                                    }
                                } }
                            )
                        }
                        NotificationType.ACTIVITY_MENTION -> {
                            appendTemplate(
                                template = resources.getString((R.string.notification_type_activity_mention_text)),
                                { link(notification.user?.nickname ?: "", linkStyle) {
                                    user?.let {
                                        onEntityClick(EntityType.User(user = user), user.id)
                                    }
                                } }
                            )
                        }
                        NotificationType.ACTIVITY_REPLY -> {
                            appendTemplate(
                                template = resources.getString((R.string.notification_type_activity_reply_text)),
                                { link(notification.user?.nickname ?: "", linkStyle) {
                                    user?.let {
                                        onEntityClick(EntityType.User(user = user), user.id)
                                    }
                                } }
                            )
                        }
                        NotificationType.ACTIVITY_REPLY_SUBSCRIBED -> {
                            appendTemplate(
                                template = resources.getString((R.string.notification_type_activity_reply_subscribed_text)),
                                { link(notification.user?.nickname ?: "", linkStyle) {
                                    user?.let {
                                        onEntityClick(EntityType.User(user = user), user.id)
                                    }
                                } }
                            )
                        }
                        NotificationType.ACTIVITY_LIKE -> {
                            appendTemplate(
                                template = resources.getString((R.string.notification_type_activity_like_text)),
                                { link(notification.user?.nickname ?: "", linkStyle) {
                                    user?.let {
                                        onEntityClick(EntityType.User(user = user), user.id)
                                    }
                                } }
                            )
                        }
                        NotificationType.ACTIVITY_REPLY_LIKE -> {
                            appendTemplate(
                                template = resources.getString((R.string.notification_type_activity_reply_like_text)),
                                { link(notification.user?.nickname ?: "", linkStyle) {
                                    user?.let {
                                        onEntityClick(EntityType.User(user = user), user.id)
                                    }
                                } }
                            )
                        }
                        else -> append(resources.getString((R.string.common_ok)))
                    }
                }
                is ThreadNotification -> {
                    when (type) {
                        NotificationType.THREAD_LIKE -> {
                            appendTemplate(
                                template = resources.getString((R.string.notification_type_thread_like_text)),
                                { link(notification.user?.nickname ?: "", linkStyle) {
                                    user?.let {
                                        onEntityClick(EntityType.User(user = user), user.id)
                                    }
                                } },
                                { link(notification.threadTitle ?: "", linkStyle) {
                                    onEntityClick(EntityType.Thread, threadId)
                                } }
                            )
                        }
                        else -> append(resources.getString((R.string.common_ok)))
                    }
                }
                is ThreadCommentNotification -> {
                    when (type) {
                        NotificationType.THREAD_COMMENT_MENTION -> {
                            appendTemplate(
                                template = resources.getString((R.string.notification_type_thread_comment_mention_text)),
                                { link(notification.user?.nickname ?: "", linkStyle) {
                                    user?.let {
                                        onEntityClick(EntityType.User(user = user), user.id)
                                    }
                                } }
                            )
                        }
                        NotificationType.THREAD_COMMENT_REPLY -> {
                            appendTemplate(
                                template = resources.getString((R.string.notification_type_thread_comment_reply_text)),
                                { link(notification.user?.nickname ?: "", linkStyle) {
                                    user?.let {
                                        onEntityClick(EntityType.User(user = user), user.id)
                                    }
                                } },
                                { link(notification.threadTitle ?: "", linkStyle) {
                                    onEntityClick(EntityType.Thread, threadId)
                                } }
                            )
                        }
                        NotificationType.THREAD_SUBSCRIBED -> {
                            appendTemplate(
                                template = resources.getString((R.string.notification_type_thread_subscribed_text)),
                                { link(notification.user?.nickname ?: "", linkStyle) {
                                    user?.let {
                                        onEntityClick(EntityType.User(user = user), user.id)
                                    }
                                } },
                                { link(notification.threadTitle ?: "", linkStyle) {
                                    onEntityClick(EntityType.Thread, threadId)
                                } }
                            )
                        }
                        NotificationType.THREAD_COMMENT_LIKE -> {
                            appendTemplate(
                                template = resources.getString((R.string.notification_type_thread_comment_like_text)),
                                { link(notification.user?.nickname ?: "", linkStyle) {
                                    user?.let {
                                        onEntityClick(EntityType.User(user = user), user.id)
                                    }
                                } },
                                { link(notification.threadTitle ?: "", linkStyle) {
                                    onEntityClick(EntityType.Thread, threadId)
                                } }
                            )
                        }
                        else -> append(resources.getString((R.string.common_ok)))
                    }
                }
                is MediaNotification -> {
                    when (type) {
                        NotificationType.RELATED_MEDIA_ADDITION -> {
                            appendTemplate(
                                template = resources.getString((R.string.notification_type_related_media_addition_text)),
                                { link(notification.mediaTitle.preferred(titleType), linkStyle) {
                                    onEntityClick(
                                        when (notification.mediaType) {
                                            MediaType.ANIME -> EntityType.Anime
                                            MediaType.MANGA -> EntityType.Manga
                                        },
                                        notification.mediaId
                                    ) }
                                }
                            )
                        }
                        NotificationType.MEDIA_DATA_CHANGE -> {
                            appendTemplate(
                                template = resources.getString((R.string.notification_type_media_data_change_text)),
                                { link(notification.mediaTitle.preferred(titleType), linkStyle) {
                                    onEntityClick(
                                        when (notification.mediaType) {
                                            MediaType.ANIME -> EntityType.Anime
                                            MediaType.MANGA -> EntityType.Manga
                                        },
                                        notification.mediaId
                                    ) }
                                }
                            )
                        }
                        else -> append(resources.getString((R.string.common_ok)))
                    }
                }
                is MediaMergeNotification -> {
                    val lastSeparator = " ${resources.getString((R.string.common_and_separator))} "

                    appendTemplate(
                        template = resources.getString((R.string.notification_type_media_merge_text)),
                        {
                            append(notification.mergedMediaTitles
                                .joinWithLast(
                                    separator = ", ",
                                    lastSeparator = lastSeparator
                                )
                            )
                        },
                        { link(notification.mediaTitle.preferred(titleType), linkStyle) {
                            onEntityClick(
                                when (notification.mediaType) {
                                    MediaType.ANIME -> EntityType.Anime
                                    MediaType.MANGA -> EntityType.Manga
                                },
                                notification.mediaId
                            ) }
                        }
                    )
                }
                is MediaDeletionNotification -> {
                    append(resources.getString(R.string.notification_type_media_deletion_text, notification.deletedMediaTitle))
                }
                is SubmissionNotification -> {
                    when (type) {
                        NotificationType.MEDIA_SUBMISSION_UPDATE,
                        NotificationType.STAFF_SUBMISSION_UPDATE,
                        NotificationType.CHARACTER_SUBMISSION_UPDATE -> {
                            append(
                                resources.getString(
                                    R.string.notification_type_submission_text,
                                    resources.getString((submissionStatus.displayValue())).lowercase()
                                )
                            )
                        }
                        else -> { append(resources.getString((R.string.common_ok))) }
                    }
                }
            }
        }
    }

    @Composable
    fun Notification.annotatedText(
        onEntityClick: (EntityType, Int) -> Unit
    ): AnnotatedString {
        val resources = LocalResources.current
        val titleType = LocalTitleTypeController.current
        val linkStyle = SpanStyle(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        return remember(this, resources, titleType, linkStyle) {
            annotatedText(resources, titleType, linkStyle, onEntityClick)
        }
    }

    private val placeholder = Regex("%(\\d+)\\$[sd]")

    private fun AnnotatedString.Builder.appendTemplate(
        template: String,
        vararg args: AnnotatedString.Builder.() -> Unit
    ) {
        var last = 0

        placeholder.findAll(template).forEach { match ->
            append(template.substring(last, match.range.first))
            args[match.groupValues[1].toInt() - 1]()
            last = match.range.last + 1
        }

        append(template.substring(last))
    }

    private fun AnnotatedString.Builder.link(
        text: String,
        style: SpanStyle,
        onClick: () -> Unit,
    ) = withLink(
        LinkAnnotation.Clickable(
            tag = text,
            styles = TextLinkStyles(style),
            linkInteractionListener = { onClick() },
        )
    ) { append(text) }
}