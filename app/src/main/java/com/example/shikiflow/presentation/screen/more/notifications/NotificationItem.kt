package com.example.shikiflow.presentation.screen.more.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import com.example.shikiflow.domain.model.notification.AiringNotification
import com.example.shikiflow.domain.model.notification.BaseNotification
import com.example.shikiflow.domain.model.notification.Notification
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.shimmerEffect

@Composable
fun NotificationItem(
    notification: Notification,
    modifier: Modifier = Modifier
) {
    when (notification) {
        is AiringNotification -> {
            AiringNotificationItem(
                airingNotification = notification,
                modifier = modifier
            )
        }
        is BaseNotification -> {
            Row(
                modifier = modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
            ) {
                BaseImage(
                    model = notification.imageUrl,
                    imageType = ImageType.Poster(
                        width = 36.dp,
                        shape = RoundedCornerShape(percent = 24)
                    )
                )

                Text(
                    text = notification.text,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
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
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .shimmerEffect(overContent = true),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
    ) {
        Box(
            modifier = Modifier
                .width(36.dp)
                .aspectRatio(2f / 2.85f)
                .background(
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(percent = 24)
                )
        )

        FlowRow(
            maxLines = 4,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(8 + indexValue * 2) { index ->
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
    }
}

@Composable
private fun AiringNotificationItem(
    airingNotification: AiringNotification,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
    ) {
        BaseImage(
            model = airingNotification.imageUrl,
            imageType = ImageType.Poster(
                width = 36.dp,
                shape = RoundedCornerShape(percent = 24)
            )
        )

        Text(
            text = buildString {
                append(airingNotification.text)
                append(" - ")
                append(airingNotification.episodeNumber)
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}