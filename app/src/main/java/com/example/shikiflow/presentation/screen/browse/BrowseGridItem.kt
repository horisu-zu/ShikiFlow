package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.BrowseCoverItem
import com.example.shikiflow.presentation.common.mappers.MediaFormatMapper.displayValue
import com.example.shikiflow.presentation.common.shimmerEffect

@Composable
fun BrowseGridItem(
    browseItem: BrowseMedia,
    titleType: PreferredTitleType,
    onItemClick: (Int, MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    val mutableInteractionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = mutableInteractionSource,
                indication = null,
                onClick = { onItemClick(browseItem.id, browseItem.mediaType) }
            )
    ) {
        BrowseCoverItem(
            posterUrl = browseItem.posterUrl,
            mediaType = browseItem.mediaType,
            userRateStatus = browseItem.userRateStatus,
            coverWidth = Int.MAX_VALUE.dp,
            cornerShape = 12.dp
        )

        Text(
            text = browseItem.title.preferred(titleType),
            style = MaterialTheme.typography.labelSmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = listOfNotNull(
                browseItem.mediaFormat?.let { mediaFormat ->
                    stringResource(id = mediaFormat.displayValue())
                },
                browseItem.score
                    ?.takeIf { it != 0.0f }
                    ?.let { score ->
                        stringResource(id = R.string.media_score, score)
                    }
            ).joinToString(" • "),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun BrowseGridItemPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 2.85f)
                .clip(RoundedCornerShape(12.dp))
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.typography.labelSmall.lineHeight.value.dp)
                .clip(RoundedCornerShape(percent = 32))
                .shimmerEffect()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(times = 2) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(MaterialTheme.typography.labelSmall.lineHeight.value.dp)
                        .clip(RoundedCornerShape(percent = 32))
                        .shimmerEffect()
                )

                if(index != 1) {
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}