package com.example.shikiflow.presentation.screen.main.details.character

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.character.MediaRole
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@Composable
fun CharacterMediaSection(
    sectionTitle: String,
    items: List<MediaRole>,
    horizontalPadding: Dp = 12.dp,
    onItemClick: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(
            modifier = Modifier.height(210.dp)
                .ignoreHorizontalParentPadding(horizontalPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items.size) { index ->
                val mediaItem = items[index]

                MediaRoleItem(
                    mediaItem = mediaItem,
                    onItemClick = onItemClick,
                    modifier = Modifier.width(120.dp)
                )
            }
        }
    }
}

@Composable
private fun MediaRoleItem(
    mediaItem: MediaRole,
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
            imageType = ImageType.Poster(
                defaultWidth = Int.MAX_VALUE.dp,
            )
        )

        Text(
            text = mediaItem.title,
            style = MaterialTheme.typography.labelSmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )

        //I'll add UserRateStatus (probably to the image like in my Shikimori Web Client)
        //and CharacterRole later
    }
}