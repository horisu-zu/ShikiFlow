package com.example.shikiflow.presentation.screen.more.profile.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.ListActivity
import com.example.shikiflow.domain.model.user.MessageActivity
import com.example.shikiflow.domain.model.user.TextActivity
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.UserActivity
import com.example.shikiflow.presentation.common.RichTextRenderer
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.ListActivityMapper.description
import com.example.shikiflow.presentation.common.mappers.ListActivityMapper.withStyledDigits
import com.example.shikiflow.presentation.common.shimmerEffect
import com.example.shikiflow.utils.Converter.convertInstantToString

@Composable
fun ActivityItem(
    userActivity: UserActivity,
    titleType: PreferredTitleType,
    onListActivityClick: (MediaType, Int) -> Unit,
    onUserClick: (User) -> Unit,
    onEntityClick: (EntityType, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    when(userActivity) {
        is ListActivity -> {
            ListActivityItem(
                listActivity = userActivity,
                titleType = titleType,
                onListActivityClick = onListActivityClick,
                modifier = modifier
            )
        }
        is MessageActivity -> {
            MessageActivityItem(
                messageActivity = userActivity,
                onUserClick = onUserClick,
                onEntityClick = onEntityClick,
                modifier = modifier
            )
        }
        is TextActivity -> {
            TextActivityItem(
                textActivity = userActivity,
                onUserClick = onUserClick,
                onEntityClick = onEntityClick,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ListActivityItem(
    listActivity: ListActivity,
    titleType: PreferredTitleType,
    onListActivityClick: (MediaType, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageType = ImageType.Poster()

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
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
    ) {
        BaseImage(
            model = listActivity.coverImage,
            contentDescription = "Poster"
        )

        Column(
            modifier = Modifier.padding(vertical = 2.dp),
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
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
    ) {
        Box(
            modifier = Modifier
                .width(imageType.width)
                .aspectRatio(imageType.aspectRatio)
                .clip(imageType.shape)
                .shimmerEffect()
        )

        Column(
            modifier = Modifier.padding(vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(MaterialTheme.typography.labelMedium.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .shimmerEffect()
            )

            Box(
                modifier = Modifier
                    .width(80.dp * (maxValue - indexValue + 1))
                    .height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .shimmerEffect()
            )

            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .width(120.dp * indexValue)
                    .height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
fun TextActivityItem(
    textActivity: TextActivity,
    onUserClick: (User) -> Unit,
    onEntityClick: (EntityType, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
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
            BaseImage(
                model = textActivity.user.avatarUrl,
                imageType = ImageType.Square(
                    width = 24.dp,
                    shape = RoundedCornerShape(8.dp)
                )
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
                text = " · ${convertInstantToString(LocalResources.current, textActivity.createdAt)}",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                ),
                maxLines = 1
            )
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
    onUserClick: (User) -> Unit,
    onEntityClick: (EntityType, Int) -> Unit,
    modifier: Modifier = Modifier
) {
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
                modifier = Modifier
                    .offset(x = (-4).dp)
                    .clip(CircleShape)
                    .clickable { onUserClick(messageActivity.messenger) }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BaseImage(
                    model = messageActivity.messenger.avatarUrl,
                    imageType = ImageType.Square(
                        width = 24.dp,
                        shape = RoundedCornerShape(8.dp)
                    )
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
                    text = " · ${convertInstantToString(LocalResources.current, messageActivity.createdAt)}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    ),
                    maxLines = 1
                )
            }

            RichTextRenderer(
                htmlText = messageActivity.text,
                style = MaterialTheme.typography.bodyMedium,
                onEntityClick = onEntityClick
            )
        }
    }
}