package com.example.shikiflow.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StarScore(
    score: Float,
    modifier: Modifier = Modifier,
    maxScore: Int = 5,
    starSize: Dp = 16.dp,
    starColor: Color = MaterialTheme.colorScheme.primary,
    emptyStarColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val scaledScore = (score / 2f).coerceIn(0f, maxScore.toFloat())

    Box(modifier = modifier) {
        Row {
            repeat(maxScore) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(starSize),
                    tint = emptyStarColor
                )
            }
        }

        Row(
            modifier = Modifier.clipToBounds()
        ) {
            repeat(maxScore) { index ->
                val starFill = when {
                    index < scaledScore.toInt() -> 1f
                    index < scaledScore -> scaledScore - index
                    else -> 0f
                }

                StarIcon(
                    fraction = starFill,
                    starSize = starSize,
                    filledColor = starColor,
                    emptyColor = emptyStarColor
                )
            }
        }
    }
}

@Composable
private fun StarIcon(
    fraction: Float,
    starSize: Dp,
    filledColor: Color,
    emptyColor: Color
) {
    Box(modifier = Modifier.size(starSize)) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            tint = emptyColor
        )

        if (fraction > 0f) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .drawWithContent {
                        clipRect(right = size.width * fraction) {
                            this@drawWithContent.drawContent()
                        }
                    },
                tint = filledColor
            )
        }
    }
}