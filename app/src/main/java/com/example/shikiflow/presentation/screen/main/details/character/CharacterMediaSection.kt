package com.example.shikiflow.presentation.screen.main.details.character

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.common.ShortMedia
import com.example.shikiflow.presentation.common.BrowseCoverItem
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.TextWithDivider
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.utils.foregroundGradient
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@Composable
fun CharacterMediaSection(
    sectionTitle: String,
    items: PaginatedList<ShortMedia>,
    horizontalPadding: Dp = 12.dp,
    onItemClick: (Int) -> Unit,
    onPaginatedNavigate: () -> Unit
) {
    val clip = 12.dp
    val imageType = ImageType.Poster(
        width = 120.dp,
        clip = RoundedCornerShape(clip),
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextWithDivider(
                text = sectionTitle,
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(
                onClick = { onPaginatedNavigate() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Navigate to Page"
                )
            }
        }

        SnapFlingLazyRow(
            modifier = Modifier
                .ignoreHorizontalParentPadding(horizontalPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items.entries.size) { index ->
                MediaRoleItem(
                    mediaItem = items.entries[index],
                    imageType = imageType,
                    cornerShape = clip,
                    onItemClick = onItemClick
                )
            }
            if(items.hasNextPage) {
                item {
                    PaginatedListNavigateIcon(
                        modifier = Modifier
                            .width(imageType.width)
                            .aspectRatio(imageType.aspectRatio)
                            .clip(imageType.clip),
                        onNavigate = { onPaginatedNavigate() }
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaRoleItem(
    mediaItem: ShortMedia,
    imageType: ImageType,
    cornerShape: Dp,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(imageType.width)
            .clip(imageType.clip)
            .clickable { onItemClick(mediaItem.id) }
    ) {
        BrowseCoverItem(
            posterUrl = mediaItem.coverImageUrl,
            mediaType = mediaItem.mediaType,
            userRateStatus = mediaItem.userRateStatus,
            coverWidth = imageType.width,
            cornerShape = cornerShape,
            isOnTop = true,
            modifier = Modifier.foregroundGradient(
                gradientColors = listOf(
                    Color.Transparent,
                    MaterialTheme.colorScheme.background
                ),
                startY = 0.6f
            )
        )

        Text(
            text = mediaItem.title,
            style = MaterialTheme.typography.labelMedium,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PaginatedListNavigateIcon(
    onNavigate: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 40.dp
) {
    Box(
        modifier = modifier
            .clickable { onNavigate() }
            .background(MaterialTheme.colorScheme.surfaceContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Navigate to Paginated Items Screen",
            modifier = Modifier.size(iconSize)
        )
    }
}