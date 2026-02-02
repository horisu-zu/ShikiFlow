package com.example.shikiflow.presentation.screen.main.details.common.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.ALComment
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.comment.ShikiComment
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.TextWithIcon
import com.example.shikiflow.presentation.common.image.RoundedImage
import com.example.shikiflow.utils.Converter.formatInstant
import com.example.shikiflow.utils.IconResource

@Composable
fun CommentItem(
    comment: Comment,
    onEntityClick: (type: EntityType, id: Int) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when(comment) {
        is ShikiComment -> {
            ShikimoriCommentItem(
                commentData = comment,
                onEntityClick = onEntityClick,
                onLinkClick = onLinkClick,
                modifier = modifier
            )
        }
        is ALComment -> {
            AnilistCommentTree(
                commentData = comment,
                onEntityClick = onEntityClick,
                onLinkClick = onLinkClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ShikimoriCommentItem(
    commentData: ShikiComment,
    onEntityClick: (type: EntityType, id: Int) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(all = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        ) {
            RoundedImage(
                model = commentData.sender?.avatarUrl,
                modifier = Modifier.size(24.dp),
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)) {
                        append(commentData.sender?.nickname)
                    }
                    append(" · ")
                    withStyle(
                        style = SpanStyle(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    ) {
                        append(formatInstant(commentData.dateTime, includeTime = true))
                    }
                }
            )

            if(commentData.isOfftopic) {
                Spacer(modifier = Modifier.weight(1f))
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
            descriptionHtml = commentData.commentBody,
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
    modifier: Modifier = Modifier,
    depth: Int = 0
) {
    val backgroundColor = when(depth % 2) {
        0 -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.background
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
            onLinkClick = onLinkClick
        )

        if(depth <= 2) {
            commentData.childComments.forEach { childComment ->
                AnilistCommentTree(
                    commentData = childComment,
                    depth = depth + 1,
                    onEntityClick = onEntityClick,
                    onLinkClick = onLinkClick
                )
            }
        } else {
            Box(
                modifier = Modifier.clip(CircleShape)
                    .clickable { onEntityClick(EntityType.COMMENT,commentData.id) }
                    .background(MaterialTheme.colorScheme.surfaceVariant)
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

@Composable
private fun AnilistCommentItem(
    commentData: ALComment,
    onEntityClick: (type: EntityType, id: Int) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        ) {
            RoundedImage(
                model = commentData.sender?.avatarUrl,
                modifier = Modifier.size(24.dp),
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)) {
                        append(commentData.sender?.nickname)
                    }
                    append(" · ")
                    withStyle(
                        style = SpanStyle(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    ) {
                        append(formatInstant(commentData.dateTime, includeTime = true))
                    }
                }
            )
        }
        ExpandableText(
            descriptionHtml = commentData.commentBody,
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(all = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundedImage(
                model = threadHeader.createdBy?.avatarUrl,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)) {
                        append(threadHeader.createdBy?.nickname)
                    }
                    append(" · ")
                    withStyle(
                        style = SpanStyle(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    ) {
                        append(formatInstant(threadHeader.createdAt, includeTime = true))
                    }
                }
            )
        }
        threadHeader.body?.let { headerBody ->
            ExpandableText(
                descriptionHtml = headerBody,
                style = MaterialTheme.typography.bodySmall,
                onEntityClick = { type, id -> onEntityClick(type, id) },
                onLinkClick = onLinkClick,
                collapsedMaxLines = Int.MAX_VALUE
            )
        }
    }
}