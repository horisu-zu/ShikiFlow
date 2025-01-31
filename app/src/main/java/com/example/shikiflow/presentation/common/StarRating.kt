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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
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
            val fullStars = scaledScore.toInt()
            val partialFill = scaledScore - fullStars

            repeat(fullStars) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(starSize),
                    tint = starColor
                )
            }

            if (partialFill > 0) {
                PartialStar(
                    fraction = partialFill,
                    starSize = starSize,
                    filledColor = starColor,
                    emptyColor = emptyStarColor
                )
            }
        }
    }
}

@Composable
private fun PartialStar(
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

        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    clip = true
                    shape = FractionalClipShape(fraction)
                }
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                tint = filledColor
            )
        }
    }
}


private class FractionalClipShape(private val fraction: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(
            rect = Rect(
                left = 0f,
                top = 0f,
                right = size.width * fraction,
                bottom = size.height
            )
        )
    }
}