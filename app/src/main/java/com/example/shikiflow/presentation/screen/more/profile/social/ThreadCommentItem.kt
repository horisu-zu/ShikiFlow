package com.example.shikiflow.presentation.screen.more.profile.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.user.social.ThreadComment
import com.example.shikiflow.presentation.screen.main.details.common.ThreadStatsItem
import com.example.shikiflow.presentation.screen.main.details.common.comment.CommentItem

@Composable
fun ThreadCommentItem(
    threadComment: ThreadComment,
    onThreadClick: () -> Unit,
    onEntityClick: (EntityType, Int) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onThreadClick() }
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            threadComment.thread.title?.let { threadTitle ->
                Text(
                    text = threadTitle,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            ThreadStatsItem(
                viewCount = threadComment.thread.viewCount,
                replyCount = threadComment.thread.replyCount,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                        alpha = 0.8f
                    )
                )
            )
        }
        CommentItem(
            comment = threadComment.comment,
            onEntityClick = onEntityClick,
            onLinkClick = onLinkClick,
            onUserClick = { /**/ },
            backgroundColor = MaterialTheme.colorScheme.background
        )
    }
}