package com.example.shikiflow.presentation.screen.more.profile.stats.anilist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.OverviewStats
import com.example.shikiflow.domain.model.user.Stat
import com.example.shikiflow.presentation.common.BarsChartMode
import com.example.shikiflow.presentation.common.HorizontalStatsBar
import com.example.shikiflow.presentation.common.SegmentedProgressBarType
import com.example.shikiflow.presentation.common.VerticalBarsChart
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.iconResource
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper.color
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper.mapStatus
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@Composable
fun AnilistMediaTracksItem(
    overviewStats: OverviewStats,
    scoreBarType: StatsBarType,
    lengthBarType: StatsBarType,
    mediaType: MediaType,
    onScoreBarTypeChange: (StatsBarType) -> Unit,
    onLengthBarTypeChange: (StatsBarType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ShortStatsOverview(
            mediaType = mediaType,
            overviewStats = overviewStats,
            modifier = Modifier.fillMaxWidth()
        )

        StatsVerticalChartComponent(
            label = R.string.details_info_score_stats,
            statsMap = mapOf(
                StatsBarType.TITLES to overviewStats.scoreStatsTitles,
                StatsBarType.TIME to overviewStats.scoreStatsTime
            ),
            mediaType = mediaType,
            currentBarType = scoreBarType,
            onBarTypeChange = { scoreBarType ->
                onScoreBarTypeChange(scoreBarType)
            }
        )

        StatsVerticalChartComponent(
            label = when (mediaType) {
                MediaType.ANIME -> R.string.user_stats_length_episode_count
                MediaType.MANGA -> R.string.user_stats_length_chapter_count
            },
            statsMap = mapOf(
                StatsBarType.TITLES to overviewStats.lengthStatsTitles,
                StatsBarType.TIME to overviewStats.lengthStatsTime,
                StatsBarType.MEAN_SCORE to overviewStats.lengthStatsScore,
            ),
            mediaType = mediaType,
            currentBarType = lengthBarType,
            onBarTypeChange = { statsBarType ->
                onLengthBarTypeChange(statsBarType)
            }
        )

        Text(
            text = stringResource(R.string.details_info_statuses_stats),
            style = MaterialTheme.typography.titleLarge
        )
        HorizontalStatsBar(
            stats = overviewStats.statusesStats,
            label = { status -> status.mapStatus(mediaType) },
            color = { status -> status.color() },
            barType = SegmentedProgressBarType.Column(
                barHeight = 16.dp,
                barShape = RoundedCornerShape(0.dp),
                horizontalPadding = 12.dp,
                arrangement = Arrangement.spacedBy(8.dp)
            )
        )
    }
}

@Composable
fun <T> StatsVerticalChartComponent(
    label: Int,
    statsMap: Map<StatsBarType, List<Stat<T>>>,
    mediaType: MediaType,
    currentBarType: StatsBarType,
    onBarTypeChange: (StatsBarType) -> Unit,
    modifier: Modifier = Modifier
) {
    val isScrollable = (statsMap[currentBarType]?.size ?: 0) > 10

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.titleLarge
        )
        TypeSelector(
            types = statsMap.keys.toList(),
            mediaType = mediaType,
            currentType = currentBarType,
            onTypeSelect = { statsBarType ->
                onBarTypeChange(statsBarType)
            }
        )
        VerticalBarsChart(
            barData = statsMap[currentBarType]?.map { scoreStat ->
                Stat(
                    type = scoreStat.type.toString(),
                    value = scoreStat.value
                )
            }?.filter { it.value > 0 } ?: emptyList(),
            chartMode = if(isScrollable) BarsChartMode.Scrollable(barWidth = 32.dp, barSpacing = 8.dp)
                else BarsChartMode.FillWidth(),
            maxBarHeight = 156.dp
        )
    }
}

@Composable
fun TypeSelector(
    types: List<StatsBarType>,
    mediaType: MediaType,
    currentType: StatsBarType,
    onTypeSelect: (StatsBarType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(all = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        types.forEach { statsBarType ->
            val isSelected = statsBarType == currentType

            Text(
                text = stringResource(statsBarType.displayValue(mediaType)),
                style = MaterialTheme.typography.labelMedium.copy(
                    textAlign = TextAlign.Center,
                    color = if(isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        if (!isSelected) {
                            onTypeSelect(statsBarType)
                        }
                    }
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
                    .padding(
                        horizontal = 4.dp,
                        vertical = 2.dp
                    )
            )
        }
    }
}

@Composable
private fun ShortStatsOverview(
    mediaType: MediaType,
    overviewStats: OverviewStats,
    modifier: Modifier = Modifier
) {
    FlowRow(
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
        modifier = modifier
    ) {
        overviewStats.shortStats.forEach { shortStat ->
            ShortStatsOverviewItem(
                label = stringResource(id = shortStat.statType.displayValue(mediaType)),
                value = shortStat.count,
                iconResource = shortStat.statType.iconResource(mediaType),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ShortStatsOverviewItem(
    label: String,
    value: String,
    iconResource: IconResource,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.onSecondary),
            contentAlignment = Alignment.Center
        ) {
            iconResource.toIcon(
                modifier = Modifier
                    .padding(6.dp)
                    .size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(top = 2.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )
    }
}

/*
@Composable
private fun StatsGraph(
    scoreStats: Map<Int, Int>,
    modifier: Modifier = Modifier
) {
    val scoreFormat = detectFormat(scoreStats)
    val modelProducer = remember { CartesianChartModelProducer() }
    val averageScore = remember(scoreStats) {
        scoreStats.entries
            .sumOf { (score, count) ->
                score * count
            } / scoreStats.values.sum().toDouble()
    }

    LaunchedEffect(scoreStats) {
        val xAxis = (scoreFormat.minVal..scoreFormat.maxVal step scoreFormat.step).toList()
        val yAxis = xAxis.map { score ->
            scoreStats[score] ?: 0
        }

        modelProducer.runTransaction {
            columnSeries {
                series(x = xAxis, y = yAxis)
            }
        }
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Text(
            text = stringResource(R.string.track_average_score, "%.2f".format(averageScore)),
            style = MaterialTheme.typography.titleMedium
        )
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                        columns = vicoTheme.columnCartesianLayerColors.map { color ->
                            rememberLineComponent(
                                fill = fill(color),
                                thickness = 24.dp,
                                shape = CorneredShape.rounded(topLeftPercent = 20, topRightPercent = 20)
                            )
                        }
                    ),
                    dataLabel = rememberTextComponent(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                ),
                startAxis = VerticalAxis.rememberStart(
                    label = null,
                    tickLength = 0.dp,
                    line = null,
                    guideline = null
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    label = rememberAxisLabelComponent(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    tickLength = 0.dp,
                    guideline = null
                )
            ),
            scrollState = rememberVicoScrollState(scrollEnabled = false),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            ),
            modelProducer = modelProducer,
            modifier = Modifier.fillMaxWidth()
        )
    }
}*/
