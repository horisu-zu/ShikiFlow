package com.example.shikiflow.presentation.screen.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.track.media.MediaTrack
import com.example.shikiflow.presentation.common.ProgressBar
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.utils.foregroundGradient

@Composable
fun AnimeTrackGridItem(
    trackItem: MediaTrack,
    onClick: (Int) -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipPercent = 8
    val imageType = ImageType.Poster(
        width = Int.MAX_VALUE.dp,
        aspectRatio = 2f / 2.6f,
        clip = RoundedCornerShape(clipPercent)
    )

    val progressBarHeight = 4.dp
    val showProgressBar = trackItem.shortData.status != MediaStatus.ANNOUNCED && trackItem.track.progress != 0

    Column(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(clipPercent))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .combinedClickable(
                onClick = { onClick(trackItem.shortData.id) },
                onLongClick = { onLongClick() }
            )
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box {
            BaseImage(
                model = trackItem.shortData.poster?.originalUrl,
                imageType = imageType,
                contentDescription = "Poster",
                modifier = Modifier
                    .clip(RoundedCornerShape(bottomStartPercent = clipPercent, bottomEndPercent = clipPercent))
                    .foregroundGradient(
                        gradientColors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background
                        ),
                        startY = 0.7f
                    )
            )
            Text(
                text = trackItem.shortData.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = clipPercent.dp, vertical = 4.dp)
            )
        }

        Text(
            text = buildString {
                val currentProgress = if(trackItem.shortData.status == MediaStatus.ONGOING)
                    "${trackItem.track.progress} / ${trackItem.shortData.currentProgress}"
                else trackItem.track.progress

                append(
                    stringResource(
                        id = R.string.ongoing_episodes,
                        currentProgress,
                        trackItem.shortData.totalCount.takeIf { (it ?: 0) > 0 } ?: "?"
                    )
                )
            },
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.padding(horizontal = clipPercent.dp)
        )

        if(showProgressBar) {
            val animeShort = trackItem.shortData
            val progress = remember(
                animeShort.totalCount,
                animeShort.currentProgress,
                trackItem.track.progress
            ) {
                val totalEpisodes = when {
                    (animeShort.totalCount ?: 0) > 0 -> animeShort.totalCount
                    (animeShort.currentProgress ?: 0) > 0 -> animeShort.currentProgress
                    else -> null
                }
                totalEpisodes?.let { (trackItem.track.progress.toFloat()) / it } ?: 0f
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
                height = progressBarHeight,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Spacer(modifier = Modifier.height(progressBarHeight))
        }
    }
}