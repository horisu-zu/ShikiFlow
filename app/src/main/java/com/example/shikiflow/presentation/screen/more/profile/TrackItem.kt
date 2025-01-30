package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shikiflow.presentation.common.SegmentedProgressBar
import com.example.shikiflow.presentation.common.TypeItem
import com.example.shikiflow.utils.IconResource

@Composable
fun TrackItem(
    type: String,
    iconResource: IconResource,
    groupedData: Map<String, Int>,
    itemsCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TypeItem(
            icon = iconResource,
            type = type,
            count = itemsCount
        )

        SegmentedProgressBar(
            groupedData = groupedData,
            totalCount = itemsCount,
        )
    }
}