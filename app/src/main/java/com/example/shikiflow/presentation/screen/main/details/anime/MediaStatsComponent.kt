package com.example.shikiflow.presentation.screen.main.details.anime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.stats.Stat
import com.example.shikiflow.presentation.common.BarsChartMode
import com.example.shikiflow.presentation.common.HorizontalStatsBar
import com.example.shikiflow.presentation.common.SegmentedProgressBarType
import com.example.shikiflow.presentation.common.VerticalBarsChart
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper.color
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper.mapStatus

enum class WindowSize {
    COMPACT, MEDIUM, EXPANDED
}

@Composable
fun MediaStatsComponent(
    mediaType: MediaType,
    isAnnounced: Boolean,
    titleScore: Float?,
    scoreStats: List<Stat<Int>>,
    statusesStats: List<Stat<UserRateStatus>>,
    modifier: Modifier = Modifier
) {
    val barHorizontalPadding = 12.dp
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val windowSize by remember(windowSizeClass) {
        derivedStateOf {
            when {
                windowSizeClass.isWidthAtLeastBreakpoint(
                    WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
                ) && windowSizeClass.isHeightAtLeastBreakpoint(
                    WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND
                ) -> WindowSize.EXPANDED
                windowSizeClass.isWidthAtLeastBreakpoint(
                    WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
                ) -> WindowSize.MEDIUM
                else -> WindowSize.COMPACT
            }
        }
    }

    when(windowSize) {
        WindowSize.EXPANDED -> {
            RowMediaStats(
                mediaType = mediaType,
                isAnnounced = isAnnounced,
                titleScore = titleScore,
                scoreStats = scoreStats,
                statusesStats = statusesStats,
                windowWidthSize = windowSize,
                barHorizontalPadding = barHorizontalPadding,
                modifier = modifier
            )
        }
        else -> {
            ColumnMediaStats(
                mediaType = mediaType,
                isAnnounced = isAnnounced,
                titleScore = titleScore,
                scoreStats = scoreStats,
                statusesStats = statusesStats,
                windowSize = windowSize,
                barHorizontalPadding = barHorizontalPadding,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ColumnMediaStats(
    mediaType: MediaType,
    isAnnounced: Boolean,
    titleScore: Float?,
    scoreStats: List<Stat<Int>>,
    statusesStats: List<Stat<UserRateStatus>>,
    windowSize: WindowSize,
    barHorizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if(!isAnnounced && titleScore != null) {
            ScoreStatsComponent(
                titleScore = titleScore,
                scoreStats = scoreStats,
                windowSize = windowSize,
                barHorizontalPadding = barHorizontalPadding,
                modifier = Modifier
            )
        }
        StatusesStatsComponent(
            mediaType = mediaType,
            statusesStats = statusesStats,
            windowSize = windowSize,
            barHorizontalPadding = barHorizontalPadding,
            modifier = Modifier
        )
    }
}

@Composable
private fun RowMediaStats(
    mediaType: MediaType,
    isAnnounced: Boolean,
    titleScore: Float?,
    scoreStats: List<Stat<Int>>,
    statusesStats: List<Stat<UserRateStatus>>,
    windowWidthSize: WindowSize,
    barHorizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var scoreComponentHeight by remember { mutableStateOf(0.dp) }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if(!isAnnounced && titleScore != null) {
            ScoreStatsComponent(
                titleScore = titleScore,
                scoreStats = scoreStats,
                windowSize = windowWidthSize,
                barHorizontalPadding = barHorizontalPadding,
                modifier = Modifier.weight(1f)
                    .onSizeChanged { size ->
                        scoreComponentHeight = with(density) { size.height.toDp() }
                    }
            )
        }
        StatusesStatsComponent(
            mediaType = mediaType,
            statusesStats = statusesStats,
            windowSize = windowWidthSize,
            barHorizontalPadding = barHorizontalPadding + 24.dp,
            modifier = Modifier
                .height(scoreComponentHeight)
                .weight(1f)
        )
    }
}

@Composable
private fun ScoreStatsComponent(
    titleScore: Float,
    scoreStats: List<Stat<Int>>,
    windowSize: WindowSize,
    barHorizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
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
            barData = scoreStats.map {
                Stat<String>(
                    type = it.type.toString(),
                    value = it.value
                )
            },
            chartMode = if(windowSize == WindowSize.COMPACT) BarsChartMode.Scrollable(
                barWidth = 32.dp,
                barSpacing = 16.dp,
                horizontalPadding = barHorizontalPadding
            ) else BarsChartMode.FillWidth(
                barFraction = 0.8f
            ),
            maxBarHeight = if(windowSize == WindowSize.EXPANDED) 96.dp else 144.dp
        )
    }
}

@Composable
private fun StatusesStatsComponent(
    mediaType: MediaType,
    statusesStats: List<Stat<UserRateStatus>>,
    windowSize: WindowSize,
    barHorizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(R.string.details_info_statuses_stats),
            style = MaterialTheme.typography.titleMedium
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(
                    start = barHorizontalPadding,
                    end = barHorizontalPadding,
                    top = barHorizontalPadding
                ),
            contentAlignment = Alignment.Center
        ) {
            HorizontalStatsBar(
                stats = statusesStats,
                label = { status -> status.mapStatus(mediaType) },
                color = { status -> status.color() },
                barType = SegmentedProgressBarType.Column(
                    barHeight = 16.dp,
                    barShape = RoundedCornerShape(0.dp),
                    horizontalPadding = barHorizontalPadding,
                    arrangement = if(windowSize == WindowSize.COMPACT) Arrangement.spacedBy(8.dp)
                        else Arrangement.SpaceBetween
                ),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}