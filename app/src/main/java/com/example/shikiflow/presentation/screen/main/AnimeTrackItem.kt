package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.track.media.MediaTrack
import com.example.shikiflow.presentation.common.ProgressBar
import com.example.shikiflow.presentation.common.StatusCard
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.mappers.MediaFormatMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaStatusMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.SeasonMapper.determineSeason

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimeTrackItem(
    userRate: MediaTrack,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = { onClick(userRate.shortData.id) },
                onLongClick = { onLongClick() }
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
    ) {
        BaseImage(
            model = userRate.shortData.poster?.originalUrl,
            contentDescription = "Poster",
            modifier = Modifier.width(96.dp)
        )

        Column(
            modifier = Modifier.padding(vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top)
        ) {
            Text(
                text = userRate.shortData.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = buildString {
                    userRate.shortData.status?.let { mediaStatus ->
                        append(stringResource(id = mediaStatus.displayValue()))
                    }
                    userRate.shortData.kind?.displayValue()?.let { formatRes ->
                        append(" • ")
                        append(stringResource(id = formatRes))
                    }
                    append(" • ")
                    append(
                        stringResource(
                            id = R.string.episodes,
                            userRate.shortData.totalCount.takeIf { (it ?: 0) > 0 } ?: "?"
                        )
                    )
                    userRate.shortData.score?.takeIf { it != 0.0f }?.let { score ->
                        append(" • ")
                        append("$score ★")
                    }
                }, style = MaterialTheme.typography.labelMedium
            )

            if (userRate.shortData.status != MediaStatus.ANNOUNCED) {
                ProgressBar(
                    progress = userRate.shortData.let { animeShort ->
                        val totalEpisodes = when {
                            (animeShort.totalCount ?: 0) > 0 -> animeShort.totalCount
                            (animeShort.currentProgress ?: 0) > 0 -> animeShort.currentProgress
                            else -> null
                        }
                        totalEpisodes?.let { (userRate.track.progress.toFloat()) / it } ?: 0f
                    },
                    modifier = Modifier.fillMaxWidth().padding(end = 8.dp)
                )
                userRate.shortData.let { animeShort ->
                    val totalEpisodes = when {
                        (animeShort.totalCount ?: 0) > 0 -> animeShort.totalCount
                        (animeShort.currentProgress ?: 0) > 0 -> animeShort.currentProgress
                        else -> 0
                    }
                    totalEpisodes
                }?.let {
                    Text(
                        text = stringResource(
                            id = R.string.ongoing_episodes,
                            userRate.track.progress,
                            it
                        ), style = MaterialTheme.typography.labelMedium
                    )
                }
            } else {
                userRate.shortData.airedOn?.let { date ->
                    StatusCard(
                        text = determineSeason(date.month)?.let { seasonRes ->
                            buildString {
                                append(stringResource(id = seasonRes))
                                append(" ${date.year}")
                            }
                        } ?: date.year.toString()
                    )
                }
            }
        }
    }
}