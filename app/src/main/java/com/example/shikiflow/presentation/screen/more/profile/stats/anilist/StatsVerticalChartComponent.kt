package com.example.shikiflow.presentation.screen.more.profile.stats.anilist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.Stat
import com.example.shikiflow.presentation.common.BarsChartMode
import com.example.shikiflow.presentation.common.VerticalBarsChart
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType

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
            maxBarHeight = 156.dp,
            mapToInt = when(currentBarType) {
                StatsBarType.MEAN_SCORE -> false
                else -> true
            }
        )
    }
}