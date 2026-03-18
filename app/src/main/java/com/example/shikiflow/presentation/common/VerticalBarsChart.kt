package com.example.shikiflow.presentation.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.user.Stat
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

sealed interface BarsChartMode {
    data class FillWidth(
        val barFraction: Float = 0.8f
    ): BarsChartMode
    data class Scrollable(
        val barWidth: Dp,
        val barSpacing: Dp,
        val horizontalPadding: Dp = 0.dp
    ): BarsChartMode
}

@Composable
fun VerticalBarsChart(
    barData: List<Stat<String>>,
    modifier: Modifier = Modifier,
    chartMode: BarsChartMode = BarsChartMode.FillWidth(),
    maxBarHeight: Dp = 120.dp,
    barColor: Color = MaterialTheme.colorScheme.primary,
    mapToInt: Boolean = true
) {
    when(chartMode) {
        is BarsChartMode.FillWidth -> {
            FixedWidthVerticalBarsChart(
                barData = barData,
                barFraction = chartMode.barFraction,
                barColor = barColor,
                maxBarHeight = maxBarHeight,
                mapToInt = mapToInt,
                modifier = modifier
            )
        }
        is BarsChartMode.Scrollable -> {
            ScrollableVerticalBarsChart(
                barData = barData,
                barColor = barColor,
                barWidth = chartMode.barWidth,
                maxBarHeight = maxBarHeight,
                barSpacing = chartMode.barSpacing,
                horizontalPadding = chartMode.horizontalPadding,
                mapToInt = mapToInt,
                modifier = modifier
            )
        }
    }
}

@Composable
fun ScrollableVerticalBarsChart(
    barData: List<Stat<String>>,
    barColor: Color,
    barWidth: Dp,
    maxBarHeight: Dp,
    barSpacing: Dp,
    horizontalPadding: Dp,
    mapToInt: Boolean,
    modifier: Modifier = Modifier
) {
    val maxValue = remember(barData) { barData.maxOfOrNull { it.value } ?: 0f }
    val autoSize = TextAutoSize.StepBased(
        minFontSize = 10.sp,
        maxFontSize = 14.sp,
        stepSize = 1.sp
    )

    Row(
        modifier = Modifier
            .ignoreHorizontalParentPadding(horizontalPadding)
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .then(modifier),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(barSpacing, Alignment.Start)
    ) {
        //Imitating LazyRow's Content Padding
        Spacer(modifier = Modifier.width(horizontalPadding - barSpacing))

        barData.forEach { (key, value) ->
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                //verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val barHeight = (value / maxValue * maxBarHeight.value)
                val textValue = if(mapToInt) value.toInt() else value

                AutoSizedText(
                    text = textValue.toString(),
                    autoSize = autoSize,
                    style = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Canvas(
                    modifier = Modifier.size(barWidth, barHeight.dp)
                ) {
                    drawRoundRect(
                        color = barColor,
                        size = size,
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )
                }
                AutoSizedText(
                    text = key.ifBlank { stringResource(R.string.common_unknown) },
                    autoSize = autoSize,
                    style = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }

        Spacer(modifier = Modifier.width(horizontalPadding - barSpacing))
    }
}

@Composable
private fun FixedWidthVerticalBarsChart(
    barData: List<Stat<String>>,
    barFraction: Float,
    barColor: Color,
    maxBarHeight: Dp,
    mapToInt: Boolean,
    modifier: Modifier = Modifier
) {
    val maxValue = remember(barData) { barData.maxOfOrNull { it.value } ?: 0f }
    val autoSize = TextAutoSize.StepBased(
        minFontSize = 10.sp,
        maxFontSize = 14.sp,
        stepSize = 1.sp
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        barData.forEach { (key, value) ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                //verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val barHeight = (value / maxValue * maxBarHeight.value).dp
                val textValue = if(mapToInt) value.toInt() else value

                AutoSizedText(
                    text = textValue.toString(),
                    autoSize = autoSize,
                    style = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth(fraction = barFraction)
                        .height(barHeight)
                ) {
                    drawRoundRect(
                        color = barColor,
                        size = size,
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )
                }
                AutoSizedText(
                    text = key.ifBlank { stringResource(R.string.common_unknown) },
                    autoSize = autoSize,
                    style = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun ScrollableChartPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Test"
        )
        ScrollableVerticalBarsChart(
            barData = listOf(
                Stat("1", value = 124f),
                Stat("2", value = 248f),
                Stat("3", value = 186f),
                Stat("4", value = 62f),
                Stat("5", value = 124f),
                Stat("6", value = 248f),
                Stat("7", value = 186f),
                Stat("8", value = 62f),
                Stat("9", value = 124f),
                Stat("10", value = 248f),
                Stat("11", value = 186f),
                Stat("12", value = 62f)
            ),
            barColor = MaterialTheme.colorScheme.primary,
            barWidth = 32.dp,
            maxBarHeight = 144.dp,
            barSpacing = 12.dp,
            horizontalPadding = 16.dp,
            mapToInt = true
        )
    }
}
