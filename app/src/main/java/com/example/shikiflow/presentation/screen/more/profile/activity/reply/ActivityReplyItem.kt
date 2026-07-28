package com.example.shikiflow.presentation.screen.more.profile.activity.reply

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.activity.ActivityReply
import com.example.shikiflow.presentation.common.RichTextRenderer
import com.example.shikiflow.presentation.screen.main.details.common.comment.CommentUserItem
import com.example.shikiflow.presentation.screen.main.details.common.comment.LikeComponent

@Composable
fun ActivityReplyItem(
    activityReply: ActivityReply,
    currentUserId: Int?,
    onUserClick: (User) -> Unit,
    onEntityClick: (EntityType, Int) -> Unit,
    onLikeToggle: (Int) -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(all = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
            CommentUserItem(
                userData = activityReply.sender,
                commentInstant = activityReply.createdAt,
                onUserClick = if (activityReply.sender.id != currentUserId) {
                    { onUserClick(activityReply.sender) }
                } else null,
                modifier = Modifier.weight(1f)
            )

            if (activityReply.sender.id == currentUserId) {
                IconButton(
                    onClick = onEditClick,
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
                    likesCount = activityReply.likeCount,
                    isLiked = activityReply.isLiked,
                    onLikeToggle = { onLikeToggle(activityReply.id) }
                )
            }
        }

        RichTextRenderer(
            htmlText = activityReply.body,
            style = MaterialTheme.typography.bodySmall,
            onEntityClick = { type, id -> onEntityClick(type, id) }
        )
    }
}