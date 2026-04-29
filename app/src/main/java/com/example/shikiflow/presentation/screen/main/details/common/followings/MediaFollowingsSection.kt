package com.example.shikiflow.presentation.screen.main.details.common.followings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.media_details.MediaFollowing
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.SnapFlingHorizontalGrid
import com.example.shikiflow.presentation.common.TextWithDivider
import com.example.shikiflow.presentation.common.TextWithIcon
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.image.shimmerEffect
import com.example.shikiflow.presentation.common.mappers.UserRateIconProvider.icon
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@Composable
fun MediaFollowingsSection(
    mediaFollowings: PaginatedList<MediaFollowing>,
    episodesCount: Int?,
    onUserClick: (User) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = if(mediaFollowings.entries.size > 1) 2 else 1

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextWithDivider(
                text = stringResource(R.string.media_details_following)
            )

            if(mediaFollowings.hasNextPage) {
                IconButton(
                    onClick = onMoreClick,
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            }
        }

        SnapFlingHorizontalGrid(
            rows = GridCells.Fixed(rows),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            modifier = Modifier.heightIn(max = (rows * 52).dp)
        ) {
            items(mediaFollowings.entries) { item ->
                MediaFollowingItem(
                    mediaFollowing = item,
                    totalCount = episodesCount,
                    onUserClick = onUserClick,
                    modifier = Modifier.width(320.dp)
                )
            }
        }
    }
}

@Composable
fun MediaFollowingItem(
    mediaFollowing: MediaFollowing,
    totalCount: Int?,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 16))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FollowingUserItem(
            userData = mediaFollowing.user,
            onUserClick = { onUserClick(mediaFollowing.user) },
            modifier = Modifier.weight(1f)
        )

        Text(
            text = buildString {
                append(mediaFollowing.progress)
                append(" / ")
                append(totalCount ?: "?")
            },
            style = MaterialTheme.typography.labelMedium
        )

        mediaFollowing.status
            .icon(mediaFollowing.mediaType)
            .toIcon(
                modifier = Modifier.size(24.dp)
            )

        mediaFollowing.score?.let { score ->
            if(score != 0.0f) {
                TextWithIcon(
                    text = buildString {
                        append(
                            when(mediaFollowing.scoreFormat) {
                                ScoreFormat.POINT_10_DECIMAL -> score
                                else -> score.toInt()
                            }
                        )
                        append(" / ")
                        append(mediaFollowing.scoreFormat.maxVal)
                    },
                    iconResources = listOf(
                        IconResource.Vector(imageVector = Icons.Default.Star)
                    ),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun FollowingUserItem(
    userData: User,
    onUserClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .offset((-6).dp)
                .clip(RoundedCornerShape(percent = 32))
                .clickable { onUserClick() }
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BaseImage(
                model = userData.avatarUrl,
                imageType = ImageType.Square(
                    clip = RoundedCornerShape(percent = 16),
                    width = 32.dp
                )
            )
            Text(
                text = userData.nickname,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }
}

@Composable
fun MediaFollowingItemPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(percent = 16))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .shimmerEffect()
    )
}