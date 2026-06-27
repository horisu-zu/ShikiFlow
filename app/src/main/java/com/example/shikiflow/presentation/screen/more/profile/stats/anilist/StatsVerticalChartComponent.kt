package com.example.shikiflow.presentation.screen.more.profile.stats.anilist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.stats.Stat
import com.example.shikiflow.presentation.common.BarsChartMode
import com.example.shikiflow.presentation.common.TextWithDivider
import com.example.shikiflow.presentation.common.TextWithDividerPlaceholder
import com.example.shikiflow.presentation.common.VerticalBarsChart
import com.example.shikiflow.presentation.common.shimmerEffect
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType
import kotlin.math.abs

@Composable
fun <T> StatsVerticalChartComponent(
    label: Int,
    statsMap: Map<StatsBarType, List<Stat<T>>>,
    mediaType: MediaType,
    currentBarType: StatsBarType,
    horizontalPadding: Dp,
    onBarTypeChange: (StatsBarType) -> Unit,
    modifier: Modifier = Modifier
) {
    val isScrollable = (statsMap[currentBarType]?.size ?: 0) > 10

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextWithDivider(
            text = stringResource(label)
        )

        TypeSelector(
            types = statsMap.keys.toList(),
            mediaType = mediaType,
            currentType = currentBarType,
            horizontalPadding = horizontalPadding,
            onTypeSelect = { statsBarType ->
                onBarTypeChange(statsBarType)
            }
        )

        VerticalBarsChart(
            barData = statsMap[currentBarType]?.map { stat ->
                Stat(
                    type = stat.type.toString(),
                    value = stat.value
                )
            }?.filter { it.value > 0 } ?: emptyList(),
            chartMode = if(isScrollable) BarsChartMode.Scrollable(
                barWidth = 32.dp,
                barSpacing = 8.dp,
                horizontalPadding = horizontalPadding
            ) else BarsChartMode.FillWidth(),
            maxBarHeight = 204.dp,
            mapToInt = when(currentBarType) {
                StatsBarType.MEAN_SCORE -> false
                else -> true
            }
        )
    }
}

@Composable
fun StatsVerticalChartComponentPlaceholder(
    itemIndex: Int,
    modifier: Modifier = Modifier,
    chartMode: BarsChartMode.FillWidth = BarsChartMode.FillWidth()
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextWithDividerPlaceholder()

        TypeSelectorPlaceholder(
            itemsCount = itemIndex + 2
        )

        StatsVerticalChartPlaceholder(
            chartMode = chartMode
        )
    }
}

@Composable
fun StatsVerticalChartPlaceholder(
    modifier: Modifier = Modifier,
    chartMode: BarsChartMode.FillWidth = BarsChartMode.FillWidth()
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(10) { index ->
            val distance = abs(index - 6f)
            val ratio = 0.1f + (1f - distance / 6f) * 0.7f

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(204.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(percent = 32))
                        .shimmerEffect()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(chartMode.barFraction)
                        .height(ratio * 160.dp)
                        .weight(1f, fill = false)
                        .clip(RoundedCornerShape(8.dp))
                        .shimmerEffect()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(percent = 32))
                        .shimmerEffect()
                )
            }
        }
    }
}