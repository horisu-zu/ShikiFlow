package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.mapStatusToString
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.CardItem
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.utils.Converter
import com.example.shikiflow.utils.StatusColor

@Composable
fun BrowseListItem(
    browseItem: Browse.Anime,
    onItemClick: (String, MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onItemClick(browseItem.id, browseItem.mediaType) },
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            contentAlignment = Alignment.TopStart
        ) {
            BaseImage(
                model = browseItem.posterUrl,
                contentScale = ContentScale.Crop,
                imageType = ImageType.Poster()
            )
            browseItem.userRateStatus ?.let {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(StatusColor.getStatusBrightColor(browseItem.userRateStatus.name))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = mapStatusToString(browseItem.userRateStatus),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.scrim,
                            fontSize = 9.sp
                        )
                    )
                }
            }
        }
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
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                append(studio)
                            }
                            if (index != browseItem.studios.lastIndex) {
                                append(" • ")
                            }
                        }
                    }, style = MaterialTheme.typography.labelSmall
                )
            }
            Text(
                text = buildString {
                    append(browseItem.kind)
                    append(" • ")
                    append("${browseItem.episodesAired} / ${browseItem.episodes.takeIf { it != 0 } ?: "?"}" )
                    append(" • ")
                    append("${browseItem.score}★")
                }, style = MaterialTheme.typography.labelSmall
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
            browseItem.nextEpisodeAt?.let { date ->
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(R.string.next_episode_at))
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            append(Converter.formatInstant(date, includeDayOfWeek = true))
                        }
                    },
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}