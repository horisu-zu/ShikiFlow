package com.example.shikiflow.presentation.screen.main.details.common.comment

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.ALComment
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.comment.ShikiComment
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.DigitCounter
import com.example.shikiflow.presentation.common.RichTextRenderer
import com.example.shikiflow.presentation.common.TextWithIcon
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.shimmerEffect
import com.example.shikiflow.utils.Converter.formatInstant
import com.example.shikiflow.utils.IconResource
import kotlin.time.Instant

@Composable
fun CommentItem(
    comment: Comment,
    currentUserId: Int,
    onEntityClick: (type: EntityType, id: Int) -> Unit,
    onUserClick: (User) -> Unit,
    onLikeToggle: (Int) -> Unit,
    onCommentSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onReplyClick: ((Int) -> Unit)? = null,
    onEditClick: ((Int, String) -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    secondBackgroundColor: Color = MaterialTheme.colorScheme.background
) {
    when(comment) {
        is ShikiComment -> {
            ShikimoriCommentItem(
                commentData = comment,
                currentUserId = currentUserId,
                onEntityClick = onEntityClick,
                onUserClick = onUserClick,
                onReplyClick = onReplyClick,
                onEditClick = onEditClick,
                modifier = modifier,
                backgroundColor = backgroundColor
            )
        }
        is ALComment -> {
            AnilistCommentTree(
                commentData = comment,
                currentUserId = currentUserId,
                onEntityClick = onEntityClick,
                onUserClick = onUserClick,
                onLikeToggle = onLikeToggle,
                onCommentSelect = onCommentSelect,
                onReplyClick = onReplyClick,
                onEditClick = onEditClick,
                modifier = modifier,
                firstBackgroundColor = backgroundColor,
                secondBackgroundColor = secondBackgroundColor
            )
        }
    }
}

@Composable
private fun ShikimoriCommentItem(
    commentData: ShikiComment,
    currentUserId: Int,
    onEntityClick: (type: EntityType, id: Int) -> Unit,
    onUserClick: (User) -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onReplyClick: ((Int) -> Unit)?,
    onEditClick: ((Int, String) -> Unit)?
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(all = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
            commentData.sender?.let { sender ->
                CommentUserItem(
                    userData = sender,
                    commentInstant = commentData.dateTime,
                    onUserClick = onUserClick,
                    modifier = Modifier.weight(1f)
                )
            }

            onReplyClick?.let {
                IconButton(
                    onClick = { onReplyClick(commentData.id) },
                    shape = RoundedCornerShape(percent = 24),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_reply),
                        contentDescription = "Reply to Comment",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (commentData.sender?.id == currentUserId && onEditClick != null) {
                IconButton(
                    onClick = { onEditClick(commentData.id, commentData.markdownBody) },
                    shape = RoundedCornerShape(percent = 24),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Comment",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if(commentData.isOfftopic) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 32))
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.75f))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.comment_offtopic),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        RichTextRenderer(
            htmlText = commentData.commentBody,
            style = MaterialTheme.typography.bodySmall,
            onEntityClick = { type, id -> onEntityClick(type, id) }
        )
    }
}

@Composable
private fun AnilistCommentTree(
    commentData: ALComment,
    currentUserId: Int,
    onEntityClick: (type: EntityType, id: Int) -> Unit,
    onUserClick: (User) -> Unit,
    onLikeToggle: (Int) -> Unit,
    onCommentSelect: (Int) -> Unit,
    firstBackgroundColor: Color,
    secondBackgroundColor: Color,
    modifier: Modifier = Modifier,
    depth: Int = 0,
    onReplyClick: ((Int) -> Unit)?,
    onEditClick: ((Int, String) -> Unit)?
) {
    val backgroundColor = when(depth % 2) {
        0 -> firstBackgroundColor
        else -> secondBackgroundColor
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(all = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        AnilistCommentItem(
            commentData = commentData,
            currentUserId = currentUserId,
            onEntityClick = onEntityClick,
            onUserClick = onUserClick,
            onLikeToggle = onLikeToggle,
            onReplyClick = onReplyClick,
            onEditClick = onEditClick
        )

        if(depth <= 2) {
            commentData.childComments.forEach { childComment ->
                AnilistCommentTree(
                    commentData = childComment,
                    currentUserId = currentUserId,
                    depth = depth + 1,
                    onEntityClick = onEntityClick,
                    onUserClick = onUserClick,
                    onLikeToggle = onLikeToggle,
                    onCommentSelect = onCommentSelect,
                    onReplyClick = onReplyClick,
                    onEditClick = onEditClick,
                    firstBackgroundColor = firstBackgroundColor,
                    secondBackgroundColor = secondBackgroundColor
                )
            }
        } else {
            if(commentData.childComments.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 32))
                        .clickable { onCommentSelect(commentData.id) }
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextWithIcon(
                        text = stringResource(R.string.comment_tree_show_more),
                        iconResources = listOf(
                            IconResource.Vector(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight)
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary
                        ),
                        placeIconAtTheBeginning = false
                    )
                }
            }
        }
    }
}

@Composable
private fun AnilistCommentItem(
    commentData: ALComment,
    currentUserId: Int,
    onEntityClick: (type: EntityType, id: Int) -> Unit,
    onUserClick: (User) -> Unit,
    onLikeToggle: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onReplyClick: ((Int) -> Unit)?,
    onEditClick: ((Int, String) -> Unit)?
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
            commentData.sender?.let { sender ->
                CommentUserItem(
                    userData = sender,
                    commentInstant = commentData.dateTime,
                    onUserClick = onUserClick,
                    modifier = Modifier.weight(1f)
                )
            }

            onReplyClick?.let {
                IconButton(
                    onClick = { onReplyClick(commentData.id) },
                    shape = RoundedCornerShape(percent = 24),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_reply),
                        contentDescription = "Reply to Comment",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (commentData.sender?.id == currentUserId && onEditClick != null) {
                IconButton(
                    onClick = { onEditClick(commentData.id, commentData.markdownBody) },
                    shape = RoundedCornerShape(percent = 24),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Comment",
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                LikeComponent(
                    likesCount = commentData.likesCount,
                    isLiked = commentData.isLiked,
                    onLikeToggle = { onLikeToggle(commentData.id) }
                )
            }
        }

        RichTextRenderer(
            htmlText = commentData.commentBody,
            style = MaterialTheme.typography.bodySmall,
            onEntityClick = { type, id -> onEntityClick(type, id) }
        )
    }
}

@Composable
fun ThreadHeaderItem(
    threadHeader: Thread,
    onEntityClick: (type: EntityType, id: Int) -> Unit,
    onUserClick: (User) -> Unit,
    onLikeToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(all = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Text(
            text = threadHeader.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 8.dp, vertical = 6.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top)
        ) {
            threadHeader.categories.forEach { category ->
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 32))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            threadHeader.createdBy?.let { threadAuthor ->
                CommentUserItem(
                    userData = threadAuthor,
                    commentInstant = threadHeader.createdAt,
                    onUserClick = onUserClick,
                    modifier = Modifier.weight(1f)
                )
            }

            LikeComponent(
                likesCount = threadHeader.likeCount,
                isLiked = threadHeader.isLiked,
                onLikeToggle = { onLikeToggle(threadHeader.id) }
            )
        }

        if (threadHeader.body.isNotEmpty()) {
            RichTextRenderer(
                htmlText = threadHeader.body,
                style = MaterialTheme.typography.bodySmall,
                onEntityClick = { type, id -> onEntityClick(type, id) }
            )
        }
    }
}

@Composable
fun ThreadHeaderItemPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(all = 12.dp)
            .shimmerEffect(overContent = true),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.typography.titleMedium.lineHeight.value.dp + 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top)
        ) {
            repeat(3) { index ->
                val indexValue = index % 2

                Box(
                    modifier = Modifier
                        .width(64.dp - indexValue * 16.dp)
                        .height(MaterialTheme.typography.bodySmall.lineHeight.value.dp + 8.dp)
                        .clip(RoundedCornerShape(percent = 32))
                        .background(MaterialTheme.colorScheme.onSurface)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CommentUserItemPlaceholder(
                itemIndex = 1,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(MaterialTheme.typography.bodySmall.lineHeight.value.dp + 12.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .background(MaterialTheme.colorScheme.onSurface)
            )
        }

        FlowRow(
            maxLines = 6,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(40) { index ->
                val indexValue = index % 8 + 1
                val itemWidth = when (indexValue <= 4) {
                    true -> 40.dp + indexValue * 12.dp
                    false -> 80.dp - indexValue * 6.dp
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
    }
}

@Composable
fun CommentUserItem(
    userData: User,
    commentInstant: Instant,
    modifier: Modifier = Modifier,
    onUserClick: ((User) -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f, fill = false)
                .offset(x = (-4).dp)
                .clip(RoundedCornerShape(percent = 32))
                .then(
                    if (onUserClick != null) {
                        Modifier.clickable { onUserClick(userData) }
                    } else Modifier
                )
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BaseImage(
                model = userData.avatarUrl,
                imageType = ImageType.Square(
                    shape = RoundedCornerShape(percent = 16),
                    width = 24.dp
                )
            )

            Text(
                text = userData.nickname,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
        }

        Text(
            text = "· ${formatInstant(commentInstant, includeTime = true)}",
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            ),
            maxLines = 1
        )
    }
}

@Composable
fun CommentItemPlaceholder(
    itemIndex: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    maxValue: Int = 3
) {
    val indexValue = itemIndex % maxValue + 1

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(all = 12.dp)
            .shimmerEffect(overContent = true),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CommentUserItemPlaceholder(
            itemIndex = itemIndex,
            modifier = Modifier.fillMaxWidth()
        )

        FlowRow(
            maxLines = 4,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(16 + indexValue * 4) { index ->
                val indexValue = index % 8 + 1
                val itemWidth = when (indexValue <= 4) {
                    true -> 40.dp + indexValue * 12.dp
                    false -> 80.dp - indexValue * 6.dp
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
    }
}

@Composable
fun LikeComponent(
    likesCount: Int,
    isLiked: Boolean,
    onLikeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val likeTint by animateColorAsState(
        targetValue = if(isLiked) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 32))
            .clickable { onLikeToggle() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(likesCount > 0) {
            DigitCounter(
                count = likesCount,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = likeTint,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Likes Count",
            tint = likeTint,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun CommentUserItemPlaceholder(
    itemIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val avatarImageType = ImageType.Square(
            width = 24.dp,
            shape = RoundedCornerShape(percent = 16)
        )

        Box(
            modifier = Modifier
                .size(avatarImageType.width)
                .clip(avatarImageType.shape)
                .background(MaterialTheme.colorScheme.onSurface)
        )

        Box(
            modifier = Modifier
                .width(72.dp + itemIndex * 16.dp)
                .height(MaterialTheme.typography.labelMedium.lineHeight.value.dp)
                .clip(RoundedCornerShape(percent = 32))
                .background(MaterialTheme.colorScheme.onSurface)
        )

        Text(
            text = "·",
            style = MaterialTheme.typography.labelMedium
        )

        Box(
            modifier = Modifier
                .width(96.dp)
                .height(MaterialTheme.typography.labelMedium.lineHeight.value.dp)
                .clip(RoundedCornerShape(percent = 32))
                .background(MaterialTheme.colorScheme.onSurface)
        )
    }
}