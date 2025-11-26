package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserRate
import com.example.shikiflow.presentation.common.SegmentedProgressBar
import com.example.shikiflow.utils.Converter.groupAndSortByStatus
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlin.collections.mapValues

@Composable
fun TrackItem(
    mediaType: MediaType,
    type: String,
    iconResource: IconResource,
    userRatesList: List<UserRate>,
    itemsCount: Int,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val groupedData = remember(userRatesList) {
        userRatesList.groupAndSortByStatus(mediaType)
    }
    val shouldShowExpand = remember(userRatesList) {
        userRatesList
            .filter { it.status == UserRateStatusEnum.completed }
            .sumOf { it.score } > 50
    }

    Column(
        modifier = modifier.fillMaxWidth()
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
            groupedData = groupedData.mapKeys { (rateResId, _) ->
                stringResource(rateResId)
            },
            totalCount = itemsCount,
            modifier = Modifier.padding(top = 8.dp)
        )

        AnimatedVisibility(visible = isExpanded) {
            val completedStatsMap = remember(userRatesList) {
                userRatesList
                    .filter { it.status == UserRateStatusEnum.completed && it.score > 0 }
                    .groupBy { it.score }
                    .mapValues { (score, userRates) -> userRates.size }
            }

            StatsGraph(
                completedStats = completedStatsMap,
                modifier = Modifier
                    .height(240.dp)
                    .padding(top = 8.dp)
            )
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

@Composable
private fun StatsGraph(
    completedStats: Map<Int, Int>,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val averageScore = remember(completedStats) {
        completedStats.entries
            .sumOf { (score, count) ->
                score * count
            } / completedStats.values.sum().toDouble()
    }

    LaunchedEffect(completedStats) {
        val xAxis = (1..10).toList()
        val yAxis = xAxis.map { score ->
            completedStats[score]?.toDouble() ?: 0.0
        }

        modelProducer.runTransaction {
            columnSeries {
                series(x = xAxis, y = yAxis)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp, vertical = 8.dp),
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
                                shape = CorneredShape.rounded(topLeftDp = 4f, topRightDp = 4f)
                            )
                        }
                    )
                ),
                startAxis = VerticalAxis.rememberStart(
                    label = rememberAxisLabelComponent(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    label = rememberAxisLabelComponent(
                        color = MaterialTheme.colorScheme.onBackground
                    )
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
}