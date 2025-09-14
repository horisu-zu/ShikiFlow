package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shikiflow.presentation.common.SegmentedProgressBar
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

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

@Composable
private fun TypeItem(
    icon: IconResource,
    type: String,
    modifier: Modifier = Modifier,
    count: Int? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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

            count?.let {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
        }
    }
}