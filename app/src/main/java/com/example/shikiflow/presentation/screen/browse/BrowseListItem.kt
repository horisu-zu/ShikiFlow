package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.BrowseCoverItem
import com.example.shikiflow.presentation.common.CardItem
import com.example.shikiflow.presentation.common.mappers.MediaFormatMapper.displayValue
import com.example.shikiflow.utils.Converter

@Composable
fun BrowseListItem(
    browseItem: BrowseMedia.Anime,
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
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = browseItem.title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                ), maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            if(browseItem.studios.isNotEmpty()) {
                Text(
                    text = buildAnnotatedString {
                        browseItem.studios.forEachIndexed { index, studio ->
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                append(studio)
                            }
                            if (index != browseItem.studios.lastIndex) {
                                append(" • ")
                            }
                        }
                    },
                    style = MaterialTheme.typography.labelSmall
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
            if(browseItem.genres.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                ) {
                    browseItem.genres.forEach { genre ->
                        CardItem(
                            item = genre,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
            browseItem.nextEpisodeAt?.let { instant ->
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.next_episode))
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            append(Converter.formatInstant(instant, includeDayOfWeek = true))
                        }
                    },
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}