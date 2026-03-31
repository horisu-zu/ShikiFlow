package com.example.shikiflow.presentation.screen.more.profile.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.user.ListActivity
import com.example.shikiflow.domain.model.user.MessageActivity
import com.example.shikiflow.domain.model.user.TextActivity
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserActivity
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.RoundedImage
import com.example.shikiflow.utils.Converter.formatInstant

@Composable
fun ActivityItem(
    userActivity: UserActivity,
    onUserClick: (User) -> Unit,
    onEntityClick: (EntityType, Int) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when(userActivity) {
        is ListActivity -> {
            ListActivityItem(
                listActivity = userActivity,
                modifier = modifier
            )
        }
        is MessageActivity -> {
            MessageActivityItem(
                messageActivity = userActivity,
                onUserClick = onUserClick,
                onEntityClick = onEntityClick,
                onLinkClick = onLinkClick,
                modifier = modifier
            )
        }
        is TextActivity -> {
            TextActivityItem(
                textActivity = userActivity,
                onUserClick = onUserClick,
                onEntityClick = onEntityClick,
                onLinkClick = onLinkClick,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ListActivityItem(
    listActivity: ListActivity,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
    ) {
        BaseImage(
            model = listActivity.coverImage,
            contentDescription = "Poster",
            modifier = Modifier.width(96.dp)
        )
        Column(
            modifier = Modifier.padding(vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = listActivity.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = listActivity.description,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = formatInstant(listActivity.createdAt, includeTime = true),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
fun TextActivityItem(
    textActivity: TextActivity,
    onUserClick: (User) -> Unit,
    onEntityClick: (EntityType, Int) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier
                .offset(x = (-4).dp)
                .clip(CircleShape)
                .clickable { onUserClick(textActivity.user) }
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundedImage(
                model = textActivity.user.avatarUrl,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = textActivity.user.nickname,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(start = 8.dp)
            )
            Text(
                text = " · ${formatInstant(textActivity.createdAt, includeTime = true)}",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                ),
                maxLines = 1
            )
        }
        ExpandableText(
            htmlText = textActivity.text,
            authType = AuthType.ANILIST,
            style = MaterialTheme.typography.bodyMedium,
            onEntityClick = onEntityClick,
            onLinkClick = onLinkClick
        )
    }
}

@Composable
fun MessageActivityItem(
    messageActivity: MessageActivity,
    onUserClick: (User) -> Unit,
    onEntityClick: (EntityType, Int) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            Row(
                modifier = Modifier
                    .offset(x = (-4).dp)
                    .clip(CircleShape)
                    .clickable { onUserClick(messageActivity.messenger) }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoundedImage(
                    model = messageActivity.messenger.avatarUrl,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = messageActivity.messenger.nickname,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(start = 8.dp)
                )
                Text(
                    text = " · ${formatInstant(messageActivity.createdAt, includeTime = true)}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    ),
                    maxLines = 1
                )
            }
            ExpandableText(
                htmlText = messageActivity.text,
                authType = AuthType.ANILIST,
                style = MaterialTheme.typography.bodyMedium,
                onEntityClick = onEntityClick,
                onLinkClick = onLinkClick
            )
        }
    }
}