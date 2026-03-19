package com.example.shikiflow.presentation.screen.more.profile.stats.shikimori

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.OverviewStats
import com.example.shikiflow.domain.model.user.Stat
import com.example.shikiflow.presentation.common.BarsChartMode
import com.example.shikiflow.presentation.common.HorizontalStatsBar
import com.example.shikiflow.presentation.common.VerticalBarsChart
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper.color
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper.mapStatus
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@Composable
fun ShikimoriTrackItem(
    mediaType: MediaType,
    type: String,
    iconResource: IconResource,
    mediaStats: OverviewStats,
    itemsCount: Int,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val shouldShowExpand = remember(mediaStats) {
        mediaStats.scoreStatsTitles.sumOf { (score, count) ->
            score * count.toInt()
        } > 50
    }

    Column(
        modifier = modifier
    ) {
        ShikimoriTypeItem(
            icon = iconResource,
            type = type,
            count = itemsCount,
            shouldShowExpand = shouldShowExpand,
            isExpanded = isExpanded,
            onExpandClick = { isExpanded = !isExpanded }
        )

        HorizontalStatsBar(
            stats = mediaStats.statusesStats,
            label = { status -> status.mapStatus(mediaType) },
            color = { status -> status.color() },
            modifier = Modifier.padding(top = 8.dp)
        )

        AnimatedVisibility(visible = isExpanded) {
            val isScrollable = mediaStats.scoreStatsTitles.size > 10
            val averageScore = remember(mediaStats.scoreStatsTitles) {
                val totalCount = mediaStats.scoreStatsTitles.sumOf { it.value.toInt() }
                if (totalCount == 0) {
                    0.0f
                } else mediaStats.scoreStatsTitles.sumOf { (score, count) ->
                    score * count.toInt()
                } / totalCount.toFloat()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(all = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
            ) {
                Text(
                    text = buildString {
                        append(stringResource(R.string.details_info_score_stats))
                        append(" ∙ ")
                        append("%.2f".format(averageScore))
                        append("★")
                    },
                    style = MaterialTheme.typography.titleMedium
                )
                VerticalBarsChart(
                    barData = mediaStats.scoreStatsTitles.map { scoreStat ->
                        Stat<String>(
                            type = scoreStat.type.toString(),
                            value = scoreStat.value
                        )
                    }.filter { it.value > 0 },
                    chartMode = if(isScrollable) BarsChartMode.Scrollable(barWidth = 32.dp, barSpacing = 8.dp)
                        else BarsChartMode.FillWidth(),
                    maxBarHeight = 156.dp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ShikimoriTypeItem(
    icon: IconResource,
    type: String,
    modifier: Modifier = Modifier,
    count: Int,
    shouldShowExpand: Boolean,
    isExpanded: Boolean,
    onExpandClick: () -> Unit
) {
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .then(
                other = if (shouldShowExpand) {
                    Modifier
                        .clickable { onExpandClick() }
                        .padding(end = 6.dp)
                } else Modifier
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.onSecondary),
            contentAlignment = Alignment.Center
        ) {
            icon.toIcon(
                modifier = Modifier
                    .padding(6.dp)
                    .size(36.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Column {
            Text(
                text = type,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
            )

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }

        if(shouldShowExpand) {
            Spacer(modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand Button",
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotationState)
            )
        }
    }
}