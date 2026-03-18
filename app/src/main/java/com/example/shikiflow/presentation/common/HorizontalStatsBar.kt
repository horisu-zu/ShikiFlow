package com.example.shikiflow.presentation.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.user.Stat
import com.example.shikiflow.utils.ignoreHorizontalParentPadding
import com.materialkolor.ktx.harmonize
import kotlin.math.roundToInt

sealed interface SegmentedProgressBarType {
    val barHeight: Dp
    val barShape: Shape

    data class Row(
        override val barHeight: Dp,
        override val barShape: Shape,
        val itemSize: Dp
    ): SegmentedProgressBarType

    data class Column(
        override val barHeight: Dp,
        override val barShape: Shape,
        val horizontalPadding: Dp,
        val arrangement: Arrangement.Horizontal
    ): SegmentedProgressBarType
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> HorizontalStatsBar(
    stats: List<Stat<T>>,
    label: (T) -> Int,
    color: (T) -> Color,
    modifier: Modifier = Modifier,
    barType: SegmentedProgressBarType = SegmentedProgressBarType.Row(
        barHeight = 12.dp,
        barShape = CircleShape,
        itemSize = 12.dp
    )
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        when(barType) {
            is SegmentedProgressBarType.Row -> {
                SegmentedProgressBarItem(
                    stats = stats,
                    color = color,
                    barHeight = barType.barHeight,
                    barShape = barType.barShape,
                    modifier = Modifier.fillMaxWidth()
                )

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    stats.forEach { (status, count) ->
                        SegmentedDataRowItem(
                            status = stringResource(id = label(status)),
                            count = count.roundToInt(),
                            size = barType.itemSize,
                            color = color(status)
                        )
                    }
                }
            }
            is SegmentedProgressBarType.Column -> {
                SnapFlingLazyRow(
                    modifier = Modifier
                        .ignoreHorizontalParentPadding(barType.horizontalPadding)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = barType.horizontalPadding),
                    horizontalArrangement = barType.arrangement
                ) {
                    items(
                        items = stats,
                        key = { it.type.toString() }
                    ) { stat ->
                        SegmentedDataColumnItem(
                            label = label(stat.type),
                            color = color(stat.type),
                            count = stat.value.roundToInt()
                        )
                    }
                }
                SegmentedProgressBarItem(
                    stats = stats,
                    color = color,
                    barHeight = barType.barHeight,
                    barShape = barType.barShape,
                    modifier = Modifier
                        .ignoreHorizontalParentPadding(barType.horizontalPadding)
                        .padding(top = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun <T> SegmentedProgressBarItem(
    stats: List<Stat<T>>,
    color: (T) -> Color,
    barHeight: Dp,
    barShape: Shape,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(barHeight)
            .clip(barShape),
        horizontalArrangement = Arrangement.spacedBy(barHeight / 4)
    ) {
        stats.forEach { (status, count) ->
            Box(
                modifier = Modifier
                    .weight(count)
                    .fillMaxHeight()
                    .background(
                        color = color(status).harmonize(MaterialTheme.colorScheme.onBackground)
                    )
            )
        }
    }
}

@Composable
private fun SegmentedDataRowItem(
    status: String,
    count: Int,
    color: Color,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            drawCircle(
                color = color.harmonize(onBackgroundColor),
                style = Fill
            )
        }
        Text(
            text = buildAnnotatedString {
                append(status)
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                ) {
                    append(" $count")
                }
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SegmentedDataColumnItem(
    label: Int,
    color: Color,
    count: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(IntrinsicSize.Max),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    color = color
                        .harmonize(MaterialTheme.colorScheme.onBackground)
                        .copy(alpha = 0.3f)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = label),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = color.harmonize(MaterialTheme.colorScheme.onBackground)
                )
            )
        }
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = color.harmonize(MaterialTheme.colorScheme.onBackground)
            )
        )
    }
}