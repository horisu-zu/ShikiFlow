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
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper.mapStatus
import com.example.shikiflow.utils.StatusColor
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

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
fun SegmentedProgressBar(
    mediaType: MediaType,
    groupedData: Map<UserRateStatus, Int>,
    totalCount: Int,
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
        //verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
    ) {
        when(barType) {
            is SegmentedProgressBarType.Row -> {
                SegmentedProgressBarItem(
                    groupedData = groupedData,
                    totalCount = totalCount,
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
                    groupedData.forEach { (status, count) ->
                        SegmentedDataRowItem(
                            status = stringResource(id = status.mapStatus(mediaType)),
                            count = count,
                            size = barType.itemSize,
                            color = StatusColor.getStatusColor(status)
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
                        items = groupedData.entries.toList(),
                        key = { it.key }
                    ) { statusStat ->
                        SegmentedDataColumnItem(
                            status = statusStat.key,
                            count = statusStat.value,
                            mediaType = mediaType
                        )
                    }
                }
                SegmentedProgressBarItem(
                    groupedData = groupedData,
                    totalCount = totalCount,
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
private fun SegmentedProgressBarItem(
    groupedData: Map<UserRateStatus, Int>,
    totalCount: Int,
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
        groupedData.forEach { (status, count) ->
            val progress = count.toFloat() / totalCount
            Box(
                modifier = Modifier
                    .weight(progress)
                    .fillMaxHeight()
                    .background(
                        color = StatusColor.getStatusColor(status)
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
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            drawCircle(
                color = color,
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
    status: UserRateStatus,
    count: Int,
    mediaType: MediaType,
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
                    color = StatusColor.getStatusColor(status).copy(alpha = 0.3f)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = status.mapStatus(mediaType)),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = StatusColor.getStatusColor(status)
                )
            )
        }
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = StatusColor.getStatusColor(status)
            )
        )
    }
}