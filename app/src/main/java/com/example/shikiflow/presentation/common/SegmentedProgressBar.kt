package com.example.shikiflow.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.shikiflow.utils.Converter
import com.example.shikiflow.utils.StatusColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SegmentedProgressBar(
    groupedData: Map<String, Int>,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (progressBarRef, dataRef) = createRefs()

        Row(
            modifier = Modifier
                .clip(CircleShape)
                .height(8.dp)
                .constrainAs(progressBarRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            horizontalArrangement = Arrangement.spacedBy(2.dp)
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
            modifier = Modifier
                .constrainAs(dataRef) {
                    top.linkTo(progressBarRef.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            groupedData.forEach { (status, count) ->
                SegmentedDataItem(
                    status = status,
                    count = count,
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = "$status $count",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}