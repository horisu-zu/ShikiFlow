package com.example.shikiflow.presentation.screen.main.details.character

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.common.ShortMedia
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@Composable
fun CharacterMediaSection(
    sectionTitle: String,
    items: PaginatedList<ShortMedia>,
    horizontalPadding: Dp = 12.dp,
    onItemClick: (Int) -> Unit,
    onPaginatedNavigate: () -> Unit
) {
    val mediaItemWidth = 120.dp
    val imageType = ImageType.Poster(
        defaultWidth = Int.MAX_VALUE.dp
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
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
                .height(210.dp)
                .ignoreHorizontalParentPadding(horizontalPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items.entries.size) { index ->
                val mediaItem = items.entries[index]

                MediaRoleItem(
                    mediaItem = mediaItem,
                    imageType = imageType,
                    onItemClick = onItemClick,
                    modifier = Modifier.width(mediaItemWidth)
                )
            }
            if(items.hasNextPage) {
                item {
                    PaginatedListNavigateIcon(
                        modifier = Modifier
                            .width(mediaItemWidth)
                            .aspectRatio(imageType.defaultAspectRatio)
                            .clip(RoundedCornerShape(12.dp)),
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
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val mutableInteractionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier.clickable(
            interactionSource = mutableInteractionSource,
            indication = null,
            onClick = { onItemClick(mediaItem.id) }
        ),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        BaseImage(
            model = mediaItem.coverImageUrl,
            contentScale = ContentScale.Crop,
            imageType = imageType
        )
        Text(
            text = mediaItem.title,
            style = MaterialTheme.typography.labelSmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
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