package com.example.shikiflow.presentation.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

enum class FadeEdge {
    TOP,
    BOTTOM
}

@Composable
fun VerticalGradientDivider(
    thickness: Dp,
    colors: List<Color>?,
    modifier: Modifier = Modifier,
    fadeEdge: FadeEdge? = null
) {
    Canvas(
        modifier = modifier
            .fillMaxHeight()
            .width(thickness)
    ) {
        val extension = thickness.toPx()

        drawLine(
            brush = Brush.verticalGradient(
                colors = colors ?: listOf(Color.Transparent, Color.Transparent)
            ),
            strokeWidth = thickness.toPx(),
            start = Offset(
                x = thickness.toPx() / 2,
                y = when(fadeEdge) {
                    FadeEdge.BOTTOM -> -extension
                    else -> 0f
                }
            ),
            end = Offset(
                x = thickness.toPx() / 2,
                y = when(fadeEdge) {
                    FadeEdge.TOP -> size.height + extension
                    else -> size.height
                }
            )
        )
    }
}