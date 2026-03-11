package com.example.shikiflow.presentation.screen.more.profile

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
import com.example.shikiflow.domain.model.user.MediaTypeStats
import com.example.shikiflow.presentation.common.BarsChartMode
import com.example.shikiflow.presentation.common.SegmentedProgressBar
import com.example.shikiflow.presentation.common.VerticalBarsChart
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@Composable
fun TrackItem(
    mediaType: MediaType,
    type: String,
    iconResource: IconResource,
    ratesList: MediaTypeStats,
    itemsCount: Int,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val shouldShowExpand = remember(ratesList) {
        ratesList.scoreStats
            .map { (score, count) -> score * count }
            .sum() > 50
    }

    Column(
        modifier = modifier
    ) {
        TypeItem(
            icon = iconResource,
            type = type,
            count = itemsCount,
            shouldShowExpand = shouldShowExpand,
            isExpanded = isExpanded,
            onExpandClick = { isExpanded = !isExpanded }
        )
        SegmentedProgressBar(
            mediaType = mediaType,
            groupedData = ratesList.statusesStats,
            totalCount = itemsCount,
            modifier = Modifier.padding(top = 8.dp)
        )

        AnimatedVisibility(visible = isExpanded) {
            val isScrollable = ratesList.scoreStats.keys.size > 10
            val averageScore = remember(ratesList.scoreStats) {
                ratesList.scoreStats.entries
                    .sumOf { (score, count) ->
                        score * count
                    } / ratesList.scoreStats.values.sum().toDouble()
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
                    barData = ratesList.scoreStats
                        .mapKeys { it.key.toString() }
                        .filter { it.value > 0 },
                    chartMode = if(isScrollable) BarsChartMode.Scrollable(barWidth = 32.dp, barSpacing = 8.dp)
                        else BarsChartMode.FillWidth(),
                    maxBarHeight = 156.dp
                )
            }
            /*StatsGraph(
                scoreStats = ratesList.scoreStats.filter { it.value > 0 },
                modifier = Modifier.fillMaxWidth()
                    .height(240.dp)
                    .padding(top = 8.dp)
            )*/
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TypeItem(
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
