package com.example.shikiflow.presentation.screen.main.details.anime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.BarsChartMode
import com.example.shikiflow.presentation.common.SegmentedProgressBar
import com.example.shikiflow.presentation.common.SegmentedProgressBarType
import com.example.shikiflow.presentation.common.VerticalBarsChart

@Composable
fun MediaStatsComponent(
    mediaType: MediaType,
    isAnnounced: Boolean,
    titleScore: Float?,
    scoreStats: Map<Int, Int>,
    statusesStats: Map<UserRateStatus, Int>
) {
    val barHorizontalPadding = 12.dp
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val hasMediumWidth by remember(windowSizeClass) {
        derivedStateOf {
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if(!isAnnounced && titleScore != null) {
            Text(
                text = buildString {
                    append(stringResource(R.string.details_info_score_stats))
                    append(" ∙ ")
                    append(titleScore)
                    append("★")
                },
                style = MaterialTheme.typography.titleMedium
            )
            VerticalBarsChart(
                barData = scoreStats.mapKeys { it.key.toString() },
                chartMode = if(hasMediumWidth) BarsChartMode.FillWidth(
                    barFraction = 0.8f
                ) else BarsChartMode.Scrollable(
                    barWidth = 32.dp,
                    barSpacing = 16.dp,
                    horizontalPadding = barHorizontalPadding
                ),
                maxBarHeight = 144.dp
            )
        }
        Text(
            text = stringResource(R.string.details_info_statuses_stats),
            style = MaterialTheme.typography.titleMedium
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(
                    start = barHorizontalPadding,
                    end = barHorizontalPadding,
                    top = 8.dp
                )
        ) {
            SegmentedProgressBar(
                mediaType = mediaType,
                groupedData = statusesStats,
                totalCount = statusesStats.size,
                barType = SegmentedProgressBarType.Column(
                    barHeight = 16.dp,
                    barShape = RoundedCornerShape(0.dp),
                    horizontalPadding = barHorizontalPadding,
                    arrangement = if(hasMediumWidth) Arrangement.SpaceBetween
                        else Arrangement.spacedBy(8.dp)
                )
            )
        }
    }
}