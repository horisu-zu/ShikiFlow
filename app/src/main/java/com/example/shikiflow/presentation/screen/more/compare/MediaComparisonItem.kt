package com.example.shikiflow.presentation.screen.more.compare

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.ComparisonType
import com.example.shikiflow.domain.model.user.MediaComparison
import com.example.shikiflow.domain.model.user.ShortUserRateData
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.ScoreFormatMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.ScoreFormatMapper.formatValue
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper.mapStatus
import com.example.shikiflow.presentation.common.shimmerEffect

@Composable
fun MediaComparisonItem(
    mediaItem: MediaComparison,
    mediaType: MediaType,
    titleType: PreferredTitleType,
    currentUserScore: ShortUserRateData?,
    targetUserScore: ShortUserRateData?,
    comparisonType: ComparisonType,
    scoreFormat: ScoreFormat?,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .padding(all = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        Row(
            modifier = Modifier
                .weight(2f)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onItemClick(mediaItem.id) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
            BaseImage(
                model = mediaItem.imageUrl,
                contentDescription = "Media Image",
                imageType = ImageType.Poster(width = 48.dp)
            )
            mediaItem.title?.let { mediaTitle ->
                Text(
                    text = mediaTitle.preferred(titleType),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        VerticalDivider(
            color = MaterialTheme.colorScheme.surfaceContainer,
            thickness = 2.dp,
            modifier = Modifier.fillMaxHeight()
        )

        if(comparisonType != ComparisonType.TARGET_USER_ONLY) {
            Text(
                text = if(
                    currentUserScore?.status == UserRateStatus.COMPLETED ||
                    currentUserScore?.status == UserRateStatus.WATCHING
                ) {
                    when(currentUserScore.userScore) {
                        0 -> { "-" }
                        else -> scoreFormat?.displayValue(
                            score = scoreFormat.formatValue(currentUserScore.userScore.toFloat())
                        ) ?: currentUserScore.userScore.toString()
                    }
                } else {
                    stringResource(
                        id = (currentUserScore?.status ?: UserRateStatus.PLANNED).mapStatus(
                            mediaType = mediaType
                        )
                    )
                },
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }

        if(comparisonType == ComparisonType.SHARED) {
            VerticalDivider(
                color = MaterialTheme.colorScheme.surfaceContainer,
                thickness = 2.dp,
                modifier = Modifier.fillMaxHeight()
            )
        }

        if(comparisonType != ComparisonType.CURRENT_USER_ONLY) {
            Text(
                text = if(
                    targetUserScore?.status == UserRateStatus.COMPLETED ||
                    targetUserScore?.status == UserRateStatus.WATCHING
                ) {
                    when(targetUserScore.userScore) {
                        0 -> { "-" }
                        else -> scoreFormat?.displayValue(
                            ScoreFormat.POINT_10_DECIMAL.formatValue(targetUserScore.userScore.toFloat())
                        ) ?: targetUserScore.userScore.toString()
                    }
                } else {
                    stringResource(
                        id = (targetUserScore?.status ?:  UserRateStatus.PLANNED).mapStatus(
                            mediaType = mediaType
                        )
                    )
                },
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MediaComparisonItemPlaceholder(
    itemIndex: Int,
    modifier: Modifier = Modifier,
    maxValue: Int = 3
) {
    val indexValue = itemIndex % maxValue + 1

    Row(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .padding(all = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
            val imageType = ImageType.Poster()

            Box(
                modifier = Modifier
                    .width(48.dp)
                    .aspectRatio(imageType.aspectRatio)
                    .clip(imageType.shape)
                    .shimmerEffect()
            )

            Box(
                modifier = Modifier
                    .width(48.dp * indexValue)
                    .height(MaterialTheme.typography.bodySmall.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .shimmerEffect()
            )
        }

        VerticalDivider(
            color = MaterialTheme.colorScheme.surfaceContainer,
            thickness = 2.dp,
            modifier = Modifier.fillMaxHeight()
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(MaterialTheme.typography.bodySmall.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .shimmerEffect()
            )
        }

        VerticalDivider(
            color = MaterialTheme.colorScheme.surfaceContainer,
            thickness = 2.dp,
            modifier = Modifier.fillMaxHeight()
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(MaterialTheme.typography.bodySmall.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .shimmerEffect()
            )
        }
    }
}