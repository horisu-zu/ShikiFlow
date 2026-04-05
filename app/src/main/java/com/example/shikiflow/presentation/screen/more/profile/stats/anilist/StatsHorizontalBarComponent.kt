package com.example.shikiflow.presentation.screen.more.profile.stats.anilist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.user.stats.Stat
import com.example.shikiflow.presentation.common.HorizontalStatsBar
import com.example.shikiflow.presentation.common.SegmentedProgressBarType
import com.example.shikiflow.presentation.common.TextWithDivider

@Composable
fun <T> StatsHorizontalBarComponent(
    label: Int,
    stats: List<Stat<T>>,
    statLabel: (T) -> Int,
    statColor: (T) -> Color,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextWithDivider(
            text = stringResource(label),
            style = MaterialTheme.typography.titleLarge
        )
        HorizontalStatsBar(
            stats = stats,
            label = statLabel,
            color = statColor,
            barType = SegmentedProgressBarType.Column(
                barHeight = 16.dp,
                barShape = RoundedCornerShape(40),
                horizontalPadding = horizontalPadding,
                arrangement = Arrangement.spacedBy(8.dp)
            )
        )
    }
}