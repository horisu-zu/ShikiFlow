package com.example.shikiflow.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.utils.StatusColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SegmentedProgressBar(
    groupedData: Map<String, Int>,
    totalCount: Int,
    modifier: Modifier = Modifier,
    rowHeight: Dp = 12.dp,
    rowShape: Shape = CircleShape,
    itemShape: Shape = CircleShape,
    itemSize: Dp = 12.dp
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(rowHeight)
                .clip(rowShape),
            horizontalArrangement = Arrangement.spacedBy(rowHeight / 4)
        ) {
            groupedData.forEach { (status, count) ->
                val progress = count.toFloat() / totalCount
                Box(
                    modifier = Modifier
                        .weight(progress)
                        .fillMaxHeight()
                        .background(
                            color = StatusColor.getStatusBrightColor(status)
                        )
                )
            }
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            groupedData.forEach { (status, count) ->
                SegmentedDataItem(
                    status = status,
                    count = count,
                    shape = itemShape,
                    size = itemSize,
                    color = StatusColor.getStatusBrightColor(status)
                )
            }
        }
    }
}

@Composable
fun SegmentedDataItem(
    status: String,
    count: Int,
    color: Color,
    shape: Shape,
    size: Dp
) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(color)
        )
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