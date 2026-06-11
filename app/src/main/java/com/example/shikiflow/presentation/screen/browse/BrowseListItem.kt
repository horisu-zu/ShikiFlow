package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.example.shikiflow.presentation.common.CardItem
import com.example.shikiflow.presentation.common.mappers.MediaFormatMapper.displayValue
import com.example.shikiflow.presentation.common.shimmerEffect
import com.example.shikiflow.utils.Converter

@Composable
fun BrowseListItem(
    browseItem: BrowseMedia.Anime,
    titleType: PreferredTitleType,
    onItemClick: (Int, MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    val roundedCornerShape = 12.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(roundedCornerShape))
            .clickable { onItemClick(browseItem.id, browseItem.mediaType) },
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalAlignment = Alignment.Top
    ) {
        BrowseCoverItem(
            posterUrl = browseItem.posterUrl,
            mediaType = browseItem.mediaType,
            userRateStatus = browseItem.userRateStatus,
            coverWidth = 96.dp,
            cornerShape = roundedCornerShape
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = browseItem.title.preferred(titleType),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if(browseItem.studios.isNotEmpty()) {
                Text(
                    text = browseItem.studios.joinToString(separator = " • "),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Text(
                text = listOfNotNull(
                    stringResource(id = browseItem.mediaFormat.displayValue()),
                    browseItem.episodesAired?.let { epsAired ->
                        "$epsAired / ${browseItem.episodes.takeIf { it != 0 } ?: "?"}"
                    },
                    browseItem.score?.let { score ->
                        stringResource(id = R.string.media_score, score)
                    }
                ).joinToString(" • "),
                style = MaterialTheme.typography.labelSmall
            )

            browseItem.nextEpisodeAt?.let { nextEpisodeInstant ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.next_episode),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = Converter.formatInstant(nextEpisodeInstant, includeDayOfWeek = true),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 32))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }

            if(browseItem.genres.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                ) {
                    browseItem.genres.forEach { genre ->
                        CardItem(
                            item = genre.preferred(titleType),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BrowseListItemPlaceholder(
    itemIndex: Int,
    modifier: Modifier = Modifier,
    maxValue: Int = 3
) {
    val indexValue = itemIndex % maxValue + 1

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .width(96.dp)
                .aspectRatio(2f / 2.85f)
                .clip(RoundedCornerShape(12.dp))
                .shimmerEffect()
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp * (maxValue - indexValue + 1))
                    .height(MaterialTheme.typography.labelLarge.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .shimmerEffect()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(times = indexValue) { index ->
                    Box(
                        modifier = Modifier
                            .width(64.dp)
                            .height(MaterialTheme.typography.labelSmall.lineHeight.value.dp)
                            .clip(RoundedCornerShape(percent = 32))
                            .shimmerEffect()
                    )

                    if(index != indexValue - 1) {
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)
            ) {
                repeat(times = maxValue - indexValue + 1) { index ->
                    Box(
                        modifier = Modifier
                            .width(36.dp * (maxValue - index))
                            .height(MaterialTheme.typography.labelSmall.lineHeight.value.dp + 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}