package com.example.shikiflow.presentation.screen.more.profile.stats.shikimori

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.stats.MediaTypeStats
import com.example.shikiflow.domain.model.user.stats.OverviewStatType
import com.example.shikiflow.domain.model.user.stats.OverviewStats
import com.example.shikiflow.presentation.common.BarsChartMode
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.iconResource
import com.example.shikiflow.presentation.common.shimmerEffect
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.StatsVerticalChartPlaceholder
import kotlin.collections.forEach

@Composable
fun ShikimoriTrackSection(
    userRateData: MediaTypeStats<OverviewStats>,
    typesList: List<MediaType>,
    currentType: MediaType,
    isLoading: Boolean,
    errorMessage: String?,
    isCurrentUser: Boolean,
    onTypeSelect: (MediaType) -> Unit,
    onRetryClick: () -> Unit,
    onCompareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        ShikimoriTrackSectionPlaceholder(
            isCurrentUser = isCurrentUser,
            modifier = modifier
        )
    } else if(errorMessage != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ErrorItem(
                message = errorMessage,
                buttonLabel = stringResource(R.string.common_retry),
                onButtonClick = { onRetryClick() }
            )
        }
    } else {
        ShikimoriTrackSectionContent(
            userRateData = userRateData,
            typesList = typesList,
            currentType = currentType,
            isCurrentUser = isCurrentUser,
            onTypeSelect = onTypeSelect,
            onCompareClick = onCompareClick,
            modifier = modifier
        )
    }
}

@Composable
private fun ShikimoriTrackSectionContent(
    userRateData: MediaTypeStats<OverviewStats>,
    typesList: List<MediaType>,
    currentType: MediaType,
    isCurrentUser: Boolean,
    onTypeSelect: (MediaType) -> Unit,
    onCompareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                typesList.forEach { mediaType ->
                    val isSelected = currentType == mediaType

                    FilterChip(
                        selected = isSelected,
                        onClick = { onTypeSelect(mediaType) },
                        label = {
                            Text(
                                text = stringResource(id = mediaType.displayValue())
                            )
                        },
                        leadingIcon = if(isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null
                                )
                            }
                        } else { null }
                    )
                }
            }
            if(!isCurrentUser) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onCompareClick() }
                        .padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.more_profile_compare),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        userRateData[currentType]?.let { mediaStats ->
            ShikimoriTrackItem(
                mediaType = currentType,
                iconResource = currentType.iconResource(),
                type = stringResource(id = currentType.displayValue()),
                mediaStats = mediaStats,
                itemsCount = mediaStats.shortStats.find { it.statType == OverviewStatType.TITLE }
                    ?.count?.toInt() ?: 0,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ShikimoriTrackSectionPlaceholder(
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
       MediaTypeListPlaceholder(
           isCurrentUser = isCurrentUser,
           modifier = Modifier.fillMaxWidth()
       )

        ShikimoriTypeItemPlaceholder()

        ShikimoriHorizontalStatsPlaceholder()

        ShikimoriVerticalStatsPlaceholder()
    }
}

@Composable
fun MediaTypeListPlaceholder(
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            repeat(2) {
                Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(percent = 24))
                        .shimmerEffect()
                )
            }
        }

        if (!isCurrentUser) {
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .height(36.dp)
                    .clip(CircleShape)
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun ShikimoriTypeItemPlaceholder(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmerEffect()
        )

        Column {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
            )

            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(MaterialTheme.typography.labelMedium.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
            )
        }
    }
}

@Composable
private fun ShikimoriHorizontalStatsPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(percent = 32))
                .shimmerEffect()
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(6) { index ->
                val indexValue = index % 4 + 1

                Row(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(percent = 24))
                            .shimmerEffect()
                    )

                    Box(
                        modifier = Modifier
                            .width(64.dp + indexValue * 16.dp)
                            .height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp)
                            .clip(RoundedCornerShape(percent = 32))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

@Composable
private fun ShikimoriVerticalStatsPlaceholder(
    modifier: Modifier = Modifier,
    chartMode: BarsChartMode.FillWidth = BarsChartMode.FillWidth()
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(MaterialTheme.typography.titleMedium.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .shimmerEffect()
            )

            Text(
                text = "·",
                style = MaterialTheme.typography.titleMedium
            )

            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(MaterialTheme.typography.titleMedium.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .shimmerEffect()
            )

            Text(
                text = "★",
                style = MaterialTheme.typography.titleMedium
            )
        }

        StatsVerticalChartPlaceholder(
            chartMode = chartMode
        )
    }
}