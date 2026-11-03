package com.example.shikiflow.presentation.screen.more.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.notification.ActivityNotification
import com.example.shikiflow.domain.model.notification.AiringNotification
import com.example.shikiflow.domain.model.notification.FollowingNotification
import com.example.shikiflow.domain.model.notification.MediaDeletionNotification
import com.example.shikiflow.domain.model.notification.MediaMergeNotification
import com.example.shikiflow.domain.model.notification.MediaNotification
import com.example.shikiflow.domain.model.notification.Notification
import com.example.shikiflow.domain.model.notification.NotificationType
import com.example.shikiflow.domain.model.notification.SubmissionNotification
import com.example.shikiflow.domain.model.notification.ThreadCommentNotification
import com.example.shikiflow.domain.model.notification.ThreadNotification
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.NotificationMapper.annotatedText
import com.example.shikiflow.presentation.common.shimmerEffect
import com.example.shikiflow.utils.Converter

@Composable
fun NotificationItem(
    notification: Notification,
    onEntityClick: (EntityType, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    when (notification) {
        is AiringNotification -> {
            AiringNotificationItem(
                airingNotification = notification,
                onEntityClick = onEntityClick,
                modifier = modifier
            )
        }
        is FollowingNotification -> {
            FollowingNotificationItem(
                followingNotification = notification,
                onEntityClick = onEntityClick,
                modifier = modifier
            )
        }
        is ActivityNotification -> {
            ActivityNotificationItem(
                activityNotification = notification,
                onEntityClick = onEntityClick,
                modifier = modifier
            )
        }
        is ThreadNotification -> {
            ThreadNotificationItem(
                threadNotification = notification,
                onEntityClick = onEntityClick,
                modifier = modifier
            )
        }
        is ThreadCommentNotification -> {
            ThreadCommentNotificationItem(
                threadCommentNotification = notification,
                onEntityClick = onEntityClick,
                modifier = modifier
            )
        }
        is SubmissionNotification -> {
            SubmissionNotificationItem(
                submissionNotification = notification,
                onEntityClick = onEntityClick,
                modifier = modifier
            )
        }
        else -> {
            Row(
                modifier = modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
            ) {
                val imageUrl = when (notification) {
                    is MediaNotification -> notification.coverImageUrl
                    is MediaMergeNotification -> notification.coverImageUrl
                    is MediaDeletionNotification -> null
                }

                imageUrl?.let {
                    BaseImage(
                        model = imageUrl,
                        imageType = ImageType.Poster(
                            width = 48.dp,
                            shape = RoundedCornerShape(12.dp)
                        ),
                        modifier = Modifier.fillMaxHeight()
                    )
                }

                Text(
                    text = notification.annotatedText(onEntityClick = onEntityClick),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp)
                )

                Text(
                    text = Converter.formatInstant(instant = notification.createdAt),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.padding(
                        top = 8.dp,
                        bottom = 8.dp,
                        end = 8.dp
                    )
                )
            }
        }
    }
}

@Composable
fun NotificationItemPlaceholder(
    itemIndex: Int,
    modifier: Modifier = Modifier,
    maxValue: Int = 3
) {
    val indexValue = itemIndex % maxValue + 1

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .shimmerEffect(overContent = true),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .aspectRatio(2f / 2.85f)
                .background(
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(12.dp)
                )
        )

        FlowRow(
            maxLines = 4,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            repeat(4 + indexValue * 2) { index ->
                val indexValue = index % 8 + 1
                val itemWidth = when (indexValue <= 4) {
                    true -> 36.dp + indexValue * 12.dp
                    false -> 72.dp - indexValue * 6.dp
                }

                Box(
                    modifier = Modifier
                        .width(itemWidth)
                        .height(MaterialTheme.typography.bodySmall.lineHeight.value.dp)
                        .clip(RoundedCornerShape(percent = 32))
                        .background(MaterialTheme.colorScheme.onSurface)
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(
                    top = 8.dp,
                    bottom = 8.dp,
                    end = 8.dp
                )
                .width(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(percent = 32)
                )
        )
    }
}

@Composable
private fun AiringNotificationItem(
    airingNotification: AiringNotification,
    onEntityClick: (EntityType, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
    ) {
        BaseImage(
            model = airingNotification.coverImageUrl,
            onClick = { onEntityClick(EntityType.Anime, airingNotification.mediaId) },
            imageType = ImageType.Poster(
                width = 48.dp,
                shape = RoundedCornerShape(12.dp)
            )
        )

        Text(
            text = airingNotification.annotatedText(onEntityClick = onEntityClick),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        )

        Text(
            text = Converter.formatInstant(instant = airingNotification.createdAt),
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            ),
            modifier = Modifier.padding(
                top = 8.dp,
                bottom = 8.dp,
                end = 8.dp
            )
        )
    }
}

@Composable
private fun FollowingNotificationItem(
    followingNotification: FollowingNotification,
    onEntityClick: (EntityType, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(all = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
    ) {
        BaseImage(
            model = followingNotification.user.avatarUrl,
            onClick = { onEntityClick(EntityType.User(followingNotification.user), followingNotification.user.id) },
            imageType = ImageType.Poster(
                width = 36.dp,
                aspectRatio = 1f,
                shape = RoundedCornerShape(12.dp)
            )
        )

        Text(
            text = followingNotification.annotatedText(onEntityClick = onEntityClick),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = Converter.formatInstant(instant = followingNotification.createdAt),
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        )
    }
}

@Composable
private fun ActivityNotificationItem(
    activityNotification: ActivityNotification,
    onEntityClick: (EntityType, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable {
                activityNotification.activity?.let {
                    onEntityClick(EntityType.Activity(activityNotification.activity.type), activityNotification.activity.id)
                }
            }.padding(all = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
        ) {
            BaseImage(
                model = activityNotification.user?.avatarUrl,
                onClick = {
                    activityNotification.user?.let { user ->
                        onEntityClick(EntityType.User(user), user.id)
                    }
                },
                imageType = ImageType.Poster(
                    width = 36.dp,
                    aspectRatio = 1f,
                    shape = RoundedCornerShape(12.dp)
                )
            )

            Text(
                text = activityNotification.annotatedText(onEntityClick = onEntityClick),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = Converter.formatInstant(instant = activityNotification.createdAt),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            )
        }

        activityNotification.activity?.let { notificationActivity ->

        }
    }
}

@Composable
private fun ThreadNotificationItem(
    threadNotification: ThreadNotification,
    onEntityClick: (EntityType, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(all = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
    ) {
        BaseImage(
            model = threadNotification.user?.avatarUrl,
            onClick = {
                threadNotification.user?.let { user ->
                    onEntityClick(EntityType.User(user), user.id)
                }
            },
            imageType = ImageType.Poster(
                width = 36.dp,
                aspectRatio = 1f,
                shape = RoundedCornerShape(12.dp)
            )
        )

        Text(
            text = threadNotification.annotatedText(onEntityClick = onEntityClick),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = Converter.formatInstant(instant = threadNotification.createdAt),
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        )
    }
}

@Composable
private fun ThreadCommentNotificationItem(
    threadCommentNotification: ThreadCommentNotification,
    onEntityClick: (EntityType, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable { onEntityClick(EntityType.CommentReply, threadCommentNotification.commentId) }
            .padding(all = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
    ) {
        BaseImage(
            model = threadCommentNotification.user?.avatarUrl,
            onClick = {
                threadCommentNotification.user?.let { user ->
                    onEntityClick(EntityType.User(user), user.id)
                }
            },
            imageType = ImageType.Poster(
                width = 36.dp,
                aspectRatio = 1f,
                shape = RoundedCornerShape(12.dp)
            )
        )

        Text(
            text = threadCommentNotification.annotatedText(onEntityClick = onEntityClick),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = Converter.formatInstant(instant = threadCommentNotification.createdAt),
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        )
    }
}

@Composable
private fun SubmissionNotificationItem(
    submissionNotification: SubmissionNotification,
    onEntityClick: (EntityType, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(all = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
    ) {
        BaseImage(
            model = submissionNotification.imageUrl,
            onClick = {
                val type = when (submissionNotification.type) {
                    NotificationType.MEDIA_SUBMISSION_UPDATE -> {
                        when (submissionNotification.mediaType) {
                            MediaType.ANIME -> EntityType.Anime
                            MediaType.MANGA -> EntityType.Manga
                            null -> null
                        }
                    }
                    NotificationType.STAFF_SUBMISSION_UPDATE -> EntityType.Staff
                    NotificationType.CHARACTER_SUBMISSION_UPDATE -> EntityType.Character
                    else -> null
                }

                type?.let {
                    onEntityClick(type, submissionNotification.contentId)
                }
            },
            imageType = ImageType.Poster(
                width = 36.dp,
                aspectRatio = 1f,
                shape = RoundedCornerShape(12.dp)
            )
        )

        Text(
            text = submissionNotification.annotatedText(onEntityClick = onEntityClick),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = Converter.formatInstant(instant = submissionNotification.createdAt),
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        )
    }
}