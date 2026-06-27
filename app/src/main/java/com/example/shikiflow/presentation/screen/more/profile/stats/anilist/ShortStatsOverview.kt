package com.example.shikiflow.presentation.screen.more.profile.stats.anilist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.stats.ShortOverviewStat
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.iconResource
import com.example.shikiflow.presentation.common.shimmerEffect
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@Composable
fun ShortStatsOverview(
    mediaType: MediaType,
    overviewStats: List<ShortOverviewStat>,
    modifier: Modifier = Modifier
) {
    FlowRow(
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
        modifier = modifier
    ) {
        overviewStats.forEach { shortStat ->
            ShortStatsOverviewItem(
                label = stringResource(id = shortStat.statType.displayValue(mediaType)),
                value = shortStat.count,
                iconResource = shortStat.statType.iconResource(mediaType),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ShortStatsOverviewItem(
    label: String,
    value: String,
    iconResource: IconResource,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.onSecondary),
            contentAlignment = Alignment.Center
        ) {
            iconResource.toIcon(
                modifier = Modifier
                    .padding(6.dp)
                    .size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(top = 2.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )
    }
}

@Composable
fun ShortStatsOverviewItemPlaceholder(
    itemIndex: Int,
    modifier: Modifier = Modifier
) {
    val indexValue = itemIndex % 3 + 1

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .width(36.dp + indexValue * 12.dp)
                .height(MaterialTheme.typography.labelMedium.lineHeight.value.dp)
                .clip(RoundedCornerShape(percent = 32))
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .width(24.dp + indexValue * 12.dp)
                .height(MaterialTheme.typography.labelMedium.lineHeight.value.dp)
                .clip(RoundedCornerShape(percent = 32))
                .shimmerEffect()
        )
    }
}