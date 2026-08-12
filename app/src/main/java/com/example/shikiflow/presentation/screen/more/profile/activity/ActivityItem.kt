package com.example.shikiflow.presentation.screen.more.profile.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.activity.ListActivity
import com.example.shikiflow.domain.model.user.activity.MessageActivity
import com.example.shikiflow.domain.model.user.activity.TextActivity
import com.example.shikiflow.domain.model.user.activity.UserActivity
import com.example.shikiflow.presentation.common.DigitCounter
import com.example.shikiflow.presentation.common.RichTextRenderer
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.ListActivityMapper.description
import com.example.shikiflow.presentation.common.mappers.ListActivityMapper.withStyledDigits
import com.example.shikiflow.presentation.common.shimmerEffect
import com.example.shikiflow.presentation.screen.main.details.common.comment.CommentUserItem
import com.example.shikiflow.presentation.screen.main.details.common.comment.LikeComponent
import com.example.shikiflow.utils.Converter.convertInstantToString
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@Composable
fun ActivityItem(
    userActivity: UserActivity,
    titleType: PreferredTitleType,
    currentUserId: Int,
    onListActivityClick: (MediaType, Int) -> Unit,
    onEntityClick: (EntityType, Int) -> Unit,
    onLikeToggle: () -> Unit,
    modifier: Modifier = Modifier,
    onRepliesClick: (() -> Unit)? = null,
    onEditClick: ((String) -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    when(userActivity) {
        is ListActivity -> {
            ListActivityItem(
                listActivity = userActivity,
                titleType = titleType,
                currentUserId = currentUserId,
                onListActivityClick = onListActivityClick,
                onLikeToggle = onLikeToggle,
                onRepliesClick = onRepliesClick,
                onDeleteClick = onDeleteClick,
                modifier = modifier
            )
        }
        is MessageActivity -> {
            MessageActivityItem(
                messageActivity = userActivity,
                currentUserId = currentUserId,
                onEntityClick = onEntityClick,
                onLikeToggle = onLikeToggle,
                onRepliesClick = onRepliesClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                modifier = modifier
            )
        }
        is TextActivity -> {
            TextActivityItem(
                textActivity = userActivity,
                currentUserId = currentUserId,
                onEntityClick = onEntityClick,
                onLikeToggle = onLikeToggle,
                onRepliesClick = onRepliesClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ListActivityItem(
    listActivity: ListActivity,
    titleType: PreferredTitleType,
    currentUserId: Int,
    onListActivityClick: (MediaType, Int) -> Unit,
    onLikeToggle: () -> Unit,
    modifier: Modifier = Modifier,
    onRepliesClick: (() -> Unit)?,
    onDeleteClick: (() -> Unit)?
) {
    val imageType = ImageType.Poster()
    val isCurrentUser = currentUserId == listActivity.userId

    Row(
        modifier = modifier
            .clip(imageType.shape)
            .then(
                if(listActivity.mediaType != null) {
                    Modifier.clickable {
                        onListActivityClick(listActivity.mediaType, listActivity.mediaId)
                    }
                } else Modifier
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        BaseImage(
            model = listActivity.coverImage,
            contentDescription = "Poster"
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = convertInstantToString(LocalResources.current, listActivity.createdAt),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )

            Text(
                text = listActivity.title.preferred(titleType),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = listActivity.description()
                    .withStyledDigits(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ),
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        if (listActivity.likeCount != null && listActivity.isLiked != null && listActivity.replyCount != null) {
            Column(
                modifier = Modifier.padding(all = 6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
                horizontalAlignment = Alignment.End
            ) {
                LikeComponent(
                    likesCount = listActivity.likeCount,
                    isLiked = listActivity.isLiked,
                    onLikeToggle = onLikeToggle,
                    enabled = !isCurrentUser,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                )

                onRepliesClick?.let {
                    CounterItem(
                        count = listActivity.replyCount,
                        iconResource = IconResource.Drawable(R.drawable.ic_bubble_filled),
                        onClick = onRepliesClick,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    )
                }

                if (isCurrentUser && onDeleteClick != null) {
                    CounterItem(
                        count = 0,
                        iconResource = IconResource.Vector(Icons.Default.Delete),
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                    )
                }
            }
        }
    }
}

@Composable
fun ListActivityItemPlaceholder(
    itemIndex: Int,
    modifier: Modifier = Modifier,
    maxValue: Int = 3
) {
    val imageType = ImageType.Poster()
    val indexValue = itemIndex % maxValue + 1

    Row(
        modifier = modifier.shimmerEffect(overContent = true),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        Box(
            modifier = Modifier
                .width(imageType.width)
                .aspectRatio(imageType.aspectRatio)
                .clip(imageType.shape)
                .background(MaterialTheme.colorScheme.onSurface)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(MaterialTheme.typography.labelMedium.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .background(MaterialTheme.colorScheme.onSurface)
            )

            Box(
                modifier = Modifier
                    .width(80.dp * (maxValue - indexValue + 1))
                    .height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .background(MaterialTheme.colorScheme.onSurface)
            )

            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .width(120.dp * indexValue)
                    .height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .background(MaterialTheme.colorScheme.onSurface)
            )
        }

        Box(
            modifier = Modifier
                .padding(all = 6.dp)
                .size(32.dp)
                .clip(RoundedCornerShape(percent = 32))
                .background(MaterialTheme.colorScheme.onSurface)
        )
    }
}

@Composable
fun TextActivityItem(
    textActivity: TextActivity,
    currentUserId: Int,
    onEntityClick: (EntityType, Int) -> Unit,
    onLikeToggle: () -> Unit,
    modifier: Modifier = Modifier,
    onRepliesClick: (() -> Unit)?,
    onEditClick: ((String) -> Unit)?,
    onDeleteClick: (() -> Unit)?
) {
    val isCurrentUser = currentUserId == textActivity.user.id

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CommentUserItem(
                userData = textActivity.user,
                commentInstant = textActivity.createdAt,
                modifier = Modifier.weight(1f)
            )

            if (textActivity.likeCount != null && textActivity.isLiked != null
                && textActivity.replyCount != null
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LikeComponent(
                        likesCount = textActivity.likeCount,
                        isLiked = textActivity.isLiked,
                        onLikeToggle = onLikeToggle,
                        enabled = !isCurrentUser
                    )

                    onRepliesClick?.let {
                        CounterItem(
                            count = textActivity.replyCount,
                            iconResource = IconResource.Drawable(R.drawable.ic_bubble_filled),
                            onClick = onRepliesClick
                        )
                    }

                    if (isCurrentUser && onEditClick != null) {
                        CounterItem(
                            count = 0,
                            iconResource = IconResource.Vector(Icons.Default.Edit),
                            onClick = { onEditClick(textActivity.markdownText) }
                        )
                    }

                    if (isCurrentUser && onDeleteClick != null) {
                        CounterItem(
                            count = 0,
                            iconResource = IconResource.Vector(Icons.Default.Delete),
                            onClick = onDeleteClick,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                        )
                    }
                }
            }
        }

        RichTextRenderer(
            htmlText = textActivity.text,
            style = MaterialTheme.typography.bodyMedium,
            onEntityClick = onEntityClick
        )
    }
}

@Composable
fun MessageActivityItem(
    messageActivity: MessageActivity,
    currentUserId: Int,
    onEntityClick: (EntityType, Int) -> Unit,
    onLikeToggle: () -> Unit,
    modifier: Modifier = Modifier,
    onRepliesClick: (() -> Unit)? = null,
    onEditClick: ((String) -> Unit)?,
    onDeleteClick: (() -> Unit)?
) {
    val isCurrentUser = currentUserId == messageActivity.messenger.id || currentUserId == messageActivity.recipient.id

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CommentUserItem(
                    userData = messageActivity.messenger,
                    commentInstant = messageActivity.createdAt,
                    modifier = Modifier.weight(1f)
                )

                if (messageActivity.likeCount != null && messageActivity.isLiked != null
                    && messageActivity.replyCount != null
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LikeComponent(
                            likesCount = messageActivity.likeCount,
                            isLiked = messageActivity.isLiked,
                            onLikeToggle = onLikeToggle,
                            enabled = !isCurrentUser
                        )

                        onRepliesClick?.let {
                            CounterItem(
                                count = messageActivity.replyCount,
                                iconResource = IconResource.Drawable(R.drawable.ic_bubble_filled),
                                onClick = onRepliesClick
                            )
                        }

                        if (currentUserId == messageActivity.messenger.id && onEditClick != null) {
                            CounterItem(
                                count = 0,
                                iconResource = IconResource.Vector(Icons.Default.Edit),
                                onClick = { onEditClick(messageActivity.markdownText) }
                            )
                        }

                        if (isCurrentUser && onDeleteClick != null) {
                            CounterItem(
                                count = 0,
                                iconResource = IconResource.Vector(Icons.Default.Delete),
                                onClick = onDeleteClick,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                            )
                        }
                    }
                }
            }

            RichTextRenderer(
                htmlText = messageActivity.text,
                style = MaterialTheme.typography.bodyMedium,
                onEntityClick = onEntityClick
            )
        }
    }
}

@Composable
private fun CounterItem(
    count: Int,
    iconResource: IconResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 32))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(count > 0) {
            DigitCounter(
                count = count,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = contentTint,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        iconResource.toIcon(
            tint = contentTint,
            modifier = Modifier.size(16.dp)
        )
    }
}