package com.example.shikiflow.presentation.screen.main.details.anime

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.presentation.common.CardItem
import com.example.shikiflow.presentation.common.mappers.MediaOriginMapper.displayValue
import com.example.shikiflow.utils.Converter.format
import com.example.shikiflow.utils.Converter.formatInstant

@Composable
fun AnimeShortInfoSection(
    animeDetails: MediaDetails,
    onStudioClick: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!animeDetails.studios.isNullOrEmpty()) {
            DetailRow(
                label = stringResource(R.string.details_info_studio),
                labelVerticalPadding = 3.dp,
                content = {
                    FlowRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                    ) {
                        animeDetails.studios.forEach { studio ->
                            CardItem(
                                item = studio.name,
                                onClick = { onStudioClick(studio.id, studio.name) }
                            )
                        }
                    }
                }
            )
        } else {
            animeDetails.origin?.let { origin ->
                DetailRow(
                    label = stringResource(R.string.details_info_origin),
                    content = {
                        Text(
                            text = stringResource(id = origin.displayValue()),
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                )
            }
        }
        if (animeDetails.durationMins != null && animeDetails.durationMins != 0) {
            DetailRow(
                label = stringResource(R.string.details_info_duration),
                content = {
                    Text(
                        text = stringResource(
                            R.string.details_info_duration_min,
                            animeDetails.durationMins
                        ),
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                }
            )
        }
        animeDetails.airedOn?.let { airedOn ->
            airedOn.format()?.let { date ->
                DetailRow(
                    label = stringResource(R.string.details_info_aired_on),
                    content = {
                        Text(
                            text = date,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                )
            }
        }
        if (animeDetails.status == MediaStatus.ONGOING) {
            animeDetails.nextEpisodeAt?.let { nextEpisodeInstant ->
                DetailRow(
                    label = stringResource(R.string.details_info_next_episode_at),
                    content = {
                        Text(
                            text = formatInstant(
                                instant = nextEpisodeInstant,
                                includeTime = true
                            ),
                            textAlign = TextAlign.End,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                )
            }
        } else if (
            animeDetails.status == MediaStatus.RELEASED
            && animeDetails.releasedOn != null
            && animeDetails.releasedOn != animeDetails.airedOn
        ) {
            animeDetails.releasedOn.format()?.let { date ->
                DetailRow(
                    label = stringResource(R.string.details_info_released_on),
                    content = {
                        Text(
                            text = date,
                            textAlign = TextAlign.End,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                )
            }
        }
        HorizontalDivider()
        DetailRow(
            label = stringResource(R.string.details_info_title_romaji),
            content = {
                Text(
                    text = animeDetails.title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
        )
        if (animeDetails.native != null) {
            DetailRow(
                label = stringResource(R.string.details_info_title_japanese),
                content = {
                    Text(
                        text = animeDetails.native,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }
            )
        }
        if (animeDetails.synonyms.isNotEmpty()) {
            DetailRow(
                label = stringResource(R.string.details_info_title_synonyms),
                content = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                    ) {
                        animeDetails.synonyms.forEach { synonym ->
                            Text(
                                text = synonym,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 2,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    labelVerticalPadding: Dp = 0.dp,
    style: TextStyle = MaterialTheme.typography.labelMedium
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
            modifier = Modifier.padding(vertical = labelVerticalPadding)
        )

        ProvideTextStyle(value = style) {
            content()
        }
    }
}