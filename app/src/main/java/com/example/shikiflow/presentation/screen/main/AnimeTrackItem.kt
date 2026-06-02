package com.example.shikiflow.presentation.screen.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.track.media.MediaTrack
import com.example.shikiflow.presentation.common.ProgressBar
import com.example.shikiflow.presentation.common.StatusCard
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.MediaFormatMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaStatusMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.SeasonMapper.determineSeason
import com.example.shikiflow.presentation.common.shimmerEffect

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimeTrackItem(
    userRate: MediaTrack,
    titleType: PreferredTitleType,
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
            )
            .padding(end = 8.dp),
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
                text = userRate.shortData.title.preferred(titleType),
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
                },
                style = MaterialTheme.typography.labelMedium
            )

            if (userRate.shortData.status != MediaStatus.ANNOUNCED) {
                val animeShort = userRate.shortData
                val totalEpisodes = when {
                    (animeShort.totalCount ?: 0) > 0 -> animeShort.totalCount
                    (animeShort.currentProgress ?: 0) > 0 -> animeShort.currentProgress
                    else -> null
                }

                val progress = remember(
                    animeShort.totalCount,
                    animeShort.currentProgress,
                    userRate.track.progress
                ) {
                    totalEpisodes?.let { (userRate.track.progress.toFloat()) / it } ?: 0f
                }

                val animatedRatio by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )

                ProgressBar(
                    progress = animatedRatio,
                    modifier = Modifier.fillMaxWidth()
                )

                totalEpisodes?.let {
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

@Composable
fun AnimeTrackItemPlaceholder(
    itemIndex: Int,
    modifier: Modifier = Modifier,
    maxValue: Int = 3
) {
    val indexValue = itemIndex % maxValue + 1
    val imageType = ImageType.Poster()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .width(imageType.width)
                .aspectRatio(imageType.aspectRatio)
                .clip(imageType.shape)
                .shimmerEffect()
        )

        Column(
            modifier = Modifier.padding(vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top)
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp / 1.5f * (maxValue - indexValue + 1))
                    .height(MaterialTheme.typography.labelLarge.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .shimmerEffect()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(MaterialTheme.typography.labelMedium.lineHeight.value.dp)
                            .clip(RoundedCornerShape(percent = 32))
                            .shimmerEffect()
                    )

                    if(index != 3) {
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerEffect()
            )
        }
    }
}