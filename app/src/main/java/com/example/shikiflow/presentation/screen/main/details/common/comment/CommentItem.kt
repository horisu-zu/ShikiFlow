package com.example.shikiflow.presentation.screen.main.details.common.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.ALComment
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.comment.ShikiComment
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.TextWithIcon
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.utils.Converter.formatInstant
import com.example.shikiflow.utils.IconResource
import kotlin.time.Instant

@Composable
fun CommentItem(
    comment: Comment,
    onEntityClick: (type: EntityType, id: Int) -> Unit,
    onLinkClick: (String) -> Unit,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    secondBackgroundColor: Color = MaterialTheme.colorScheme.background
) {
    when(comment) {
        is ShikiComment -> {
            ShikimoriCommentItem(
                commentData = comment,
                onEntityClick = onEntityClick,
                onLinkClick = onLinkClick,
                onUserClick = onUserClick,
                modifier = modifier,
                backgroundColor = backgroundColor
            )
        }
        is ALComment -> {
            AnilistCommentTree(
                commentData = comment,
                onEntityClick = onEntityClick,
                onLinkClick = onLinkClick,
                onUserClick = onUserClick,
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
    onEntityClick: (type: EntityType, id: Int) -> Unit,
    onLinkClick: (String) -> Unit,
    onUserClick: (User) -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
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
            if(commentData.isOfftopic) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
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
        ExpandableText(
            htmlText = commentData.commentBody,
            authType = AuthType.SHIKIMORI,
            style = MaterialTheme.typography.bodySmall,
            onEntityClick = { type, id -> onEntityClick(type, id) },
            onLinkClick = onLinkClick,
            collapsedMaxLines = Int.MAX_VALUE
        )
    }
}

@Composable
private fun AnilistCommentTree(
    commentData: ALComment,
    onEntityClick: (type: EntityType, id: Int) -> Unit,
    onLinkClick: (String) -> Unit,
    onUserClick: (User) -> Unit,
    firstBackgroundColor: Color,
    secondBackgroundColor: Color,
    modifier: Modifier = Modifier,
    depth: Int = 0
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
            onEntityClick = onEntityClick,
            onLinkClick = onLinkClick,
            onUserClick = onUserClick
        )

        if(depth <= 2) {
            commentData.childComments.forEach { childComment ->
                AnilistCommentTree(
                    commentData = childComment,
                    depth = depth + 1,
                    onEntityClick = onEntityClick,
                    onLinkClick = onLinkClick,
                    onUserClick = onUserClick,
                    firstBackgroundColor = firstBackgroundColor,
                    secondBackgroundColor = secondBackgroundColor
                )
            }
        } else {
            if(commentData.childComments.isNotEmpty()) {
                Box(
                    modifier = Modifier.clip(CircleShape)
                        .clickable { onEntityClick(EntityType.COMMENT,commentData.id) }
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
    onEntityClick: (type: EntityType, id: Int) -> Unit,
    onLinkClick: (String) -> Unit,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
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

            if(commentData.likesCount > 0) { //Could be temporary if I decide to integrate a request
                //Plan to add isLiked field and different coloring depending on its value
                Row(
                    modifier = Modifier.clip(CircleShape)
                        //.background(MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = commentData.likesCount.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Likes Count",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        ExpandableText(
            htmlText = commentData.commentBody,
            authType = AuthType.ANILIST,
            style = MaterialTheme.typography.bodySmall,
            onEntityClick = { type, id -> onEntityClick(type, id) },
            onLinkClick = onLinkClick,
            collapsedMaxLines = Int.MAX_VALUE
        )
    }
}

@Composable
fun ThreadHeaderItem(
    threadHeader: Thread,
    onEntityClick: (type: EntityType, id: Int) -> Unit,
    onLinkClick: (String) -> Unit,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(all = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        threadHeader.title?.let { threadTitle ->
            Box(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = threadTitle,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        threadHeader.createdBy?.let { threadAuthor ->
            CommentUserItem(
                userData = threadAuthor,
                commentInstant = threadHeader.createdAt,
                onUserClick = onUserClick
            )
        }
        threadHeader.body?.let { headerBody ->
            ExpandableText(
                htmlText = headerBody,
                authType = AuthType.ANILIST,
                style = MaterialTheme.typography.bodySmall,
                onEntityClick = { type, id -> onEntityClick(type, id) },
                onLinkClick = onLinkClick,
                collapsedMaxLines = Int.MAX_VALUE
            )
        }
    }
}

@Composable
private fun CommentUserItem(
    userData: User,
    commentInstant: Instant,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .offset(x = (-4).dp)
                .clip(CircleShape)
                .clickable { onUserClick(userData) }
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BaseImage(
                model = userData.avatarUrl,
                imageType = ImageType.Square(
                    clip = RoundedCornerShape(percent = 16),
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