package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChapterProgressBarComponent(
    currentPage: Int,
    pageCount: Int,
    onSegmentClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val animatedProgress by animateFloatAsState(
        targetValue = currentPage.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Row(
        modifier = Modifier
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 6.dp,
                bottom = 4.dp
            )
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(pageCount) { index ->
            val segmentFillFraction = (animatedProgress - index).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(percent = 40))
                    .background(inactiveColor)
                    .clickable {
                        onSegmentClick(index + 1)
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = segmentFillFraction)
                        .fillMaxHeight()
                        .background(activeColor)
                )
            }
        }
    }
}