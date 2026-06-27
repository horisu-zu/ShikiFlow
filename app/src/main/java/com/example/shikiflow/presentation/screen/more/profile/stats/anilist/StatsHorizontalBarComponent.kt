package com.example.shikiflow.presentation.screen.more.profile.stats.anilist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.user.stats.Stat
import com.example.shikiflow.presentation.common.HorizontalStatsBar
import com.example.shikiflow.presentation.common.SegmentedProgressBarType
import com.example.shikiflow.presentation.common.TextWithDivider
import com.example.shikiflow.presentation.common.TextWithDividerPlaceholder
import com.example.shikiflow.presentation.common.ignoreHorizontalParentPadding
import com.example.shikiflow.presentation.common.shimmerEffect

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
            text = stringResource(label)
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

@Composable
fun StatsHorizontalBarComponentPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextWithDividerPlaceholder()

        StatsHorizontalBarPlaceholder()
    }
}

@Composable
fun StatsHorizontalBarPlaceholder(
    modifier: Modifier = Modifier,
    barType: SegmentedProgressBarType.Column = SegmentedProgressBarType.Column(
        barHeight = 16.dp,
        barShape = RoundedCornerShape(40),
        horizontalPadding = 12.dp,
        arrangement = Arrangement.spacedBy(8.dp)
    )
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = barType.arrangement
        ) {
            repeat(5) {
                Column(
                    modifier = modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp + 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .shimmerEffect()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(MaterialTheme.typography.bodySmall.lineHeight.value.dp)
                            .clip(RoundedCornerShape(percent = 32))
                            .shimmerEffect()
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .ignoreHorizontalParentPadding(barType.horizontalPadding)
                .fillMaxWidth()
                .height(barType.barHeight)
                .clip(barType.barShape)
                .shimmerEffect()
        )
    }
}