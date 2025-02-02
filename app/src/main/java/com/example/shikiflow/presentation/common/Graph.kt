package com.example.shikiflow.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filter
import kotlin.math.ceil

sealed interface GraphStyle {
    data class Bar(
        val color: Color,
        val alpha: Float = 1f
    ) : GraphStyle

    data class Axis(
        val color: Color,
        val strokeWidth: Float,
        val gridColor: Color = color.copy(alpha = 0.2f)
    ) : GraphStyle

    data class Text(
        val color: Color,
        val size: Float
    ) : GraphStyle
}

data class GraphDimensions(
    val axisPadding: Float = 60f,
    val contentPadding: Float = 16f,
    val minBarSize: Float = 25f,
    val minSpacing: Float = 15f
)

enum class GraphGridType {
    NONE, HORIZONTAL, VERTICAL, BOTH
}

private class GraphState(
    var canvasSize: Size = Size.Zero
)

@Composable
fun Graph(
    data: Map<String, Float>,
    modifier: Modifier = Modifier,
    height: Dp = 240.dp,
    gridType: GraphGridType = GraphGridType.BOTH,
    dimensions: GraphDimensions = GraphDimensions(),
    style: GraphStyle.Bar = GraphStyle.Bar(
        color = MaterialTheme.colorScheme.primary
    ),
    axisStyle: GraphStyle.Axis = GraphStyle.Axis(
        color = MaterialTheme.colorScheme.outline,
        strokeWidth = 2.dp.value
    ),
    textStyle: GraphStyle.Text = GraphStyle.Text(
        color = MaterialTheme.colorScheme.onSurface,
        size = 24f
    )
) {
    var isVisible by remember { mutableStateOf(false) }
    val animatedProgress = remember { Animatable(0f) }
    val graphState = remember { GraphState() }
    val view = LocalView.current

    LaunchedEffect(Unit) {
        snapshotFlow { isVisible }
            .filter { it }
            .collect {
                animatedProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = FastOutSlowInEasing
                    )
                )
            }
    }

    Box(
        modifier = modifier
            .onPlaced {
                val rect = it.boundsInWindow()
                isVisible = rect.top < view.height && rect.bottom > 0
            }
    ) {
        GraphCanvas(
            data = data,
            dimensions = dimensions,
            style = style,
            axisStyle = axisStyle,
            textStyle = textStyle,
            progress = animatedProgress.value,
            gridType = gridType,
            state = graphState,
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        )
    }
}

@Composable
private fun GraphCanvas(
    data: Map<String, Float>,
    dimensions: GraphDimensions,
    style: GraphStyle.Bar,
    axisStyle: GraphStyle.Axis,
    textStyle: GraphStyle.Text,
    progress: Float,
    gridType: GraphGridType,
    state: GraphState,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.onSizeChanged {
            state.canvasSize = Size(it.width.toFloat(), it.height.toFloat())
        }
    ) {
        if (data.isEmpty()) return@Canvas

        val maxValue = data.values.maxOrNull() ?: 0f
        val roundedMaxValue = calculateRoundedMaxValue(maxValue)

        drawAxes(
            dimensions = dimensions,
            style = axisStyle,
            textStyle = textStyle,
            gridType = gridType,
            maxValue = roundedMaxValue
        )

        drawValueLabels(
            maxValue = roundedMaxValue,
            dimensions = dimensions,
            textStyle = textStyle
        )

        drawBars(
            data = data,
            dimensions = dimensions,
            style = style,
            maxValue = roundedMaxValue,
            progress = progress
        )

        drawLabels(
            data = data,
            dimensions = dimensions,
            textStyle = textStyle
        )
    }
}

private fun calculateRoundedMaxValue(value: Float): Float = when {
    value > 1000 -> ceil(value / 100) * 100
    value > 100 -> ceil(value / 10) * 10
    else -> ceil(value)
}

private fun calculateBarSizes(
    totalBars: Int,
    dimensions: GraphDimensions,
    size: Size
): Pair<Float, Float> {
    val availableSize = size.width - 2 * dimensions.axisPadding
    val barSize = (availableSize / totalBars * 0.7f).coerceAtLeast(dimensions.minBarSize)
    val spacing = (availableSize / totalBars * 0.3f).coerceAtLeast(dimensions.minSpacing)
    return barSize to spacing
}

private fun DrawScope.drawAxes(
    dimensions: GraphDimensions,
    style: GraphStyle.Axis,
    gridType: GraphGridType,
    textStyle: GraphStyle.Text,
    maxValue: Float
) {
    val axisPath = Path().apply {
        moveTo(dimensions.axisPadding, dimensions.contentPadding)
        lineTo(dimensions.axisPadding, size.height - dimensions.axisPadding)
        lineTo(size.width - dimensions.contentPadding, size.height - dimensions.axisPadding)
    }

    drawPath(
        path = axisPath,
        color = style.color,
        style = Stroke(width = style.strokeWidth)
    )

    drawGrid(
        dimensions = dimensions,
        gridType = gridType,
        style = style,
        maxValue = maxValue
    )
}

private fun DrawScope.drawGrid(
    dimensions: GraphDimensions,
    style: GraphStyle.Axis,
    maxValue: Float,
    gridType: GraphGridType
) {
    if (gridType == GraphGridType.NONE) return

    val steps = 5
    val stepValue = maxValue / steps

    repeat(steps + 1) { i ->
        val position = i * stepValue

        if (gridType == GraphGridType.HORIZONTAL || gridType == GraphGridType.BOTH) {
            val y = size.height - dimensions.axisPadding -
                    (position / maxValue) * (size.height - 2 * dimensions.axisPadding)
            drawLine(
                color = style.gridColor,
                start = Offset(dimensions.axisPadding, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        if (gridType == GraphGridType.VERTICAL || gridType == GraphGridType.BOTH) {
            val x = dimensions.axisPadding +
                    (position / maxValue) * (size.width - 2 * dimensions.axisPadding)
            drawLine(
                color = style.gridColor,
                start = Offset(x, dimensions.axisPadding),
                end = Offset(x, size.height),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

private fun DrawScope.drawBars(
    data: Map<String, Float>,
    dimensions: GraphDimensions,
    style: GraphStyle.Bar,
    maxValue: Float,
    progress: Float
) {
    val (barSize, spacing) = calculateBarSizes(
        totalBars = data.size,
        dimensions = dimensions,
        size = size
    )

    data.entries.forEachIndexed { index, entry ->
        val normalizedValue = (entry.value / maxValue) * progress
        val x = dimensions.axisPadding + index * (barSize + spacing) + spacing / 2
        val height = normalizedValue * (size.height - 2 * dimensions.axisPadding)

        drawRect(
            color = style.color.copy(alpha = style.alpha),
            topLeft = Offset(x, size.height - dimensions.axisPadding - height),
            size = Size(barSize, height)
        )
    }
}

private fun DrawScope.drawLabels(
    data: Map<String, Float>,
    dimensions: GraphDimensions,
    textStyle: GraphStyle.Text
) {
    val (barSize, spacing) = calculateBarSizes(
        totalBars = data.size,
        dimensions = dimensions,
        size = size
    )

    val paint = Paint().asFrameworkPaint().apply {
        color = textStyle.color.toArgb()
        this.textSize = textStyle.size
        textAlign = android.graphics.Paint.Align.CENTER
    }

    data.keys.forEachIndexed { index, label ->
        val slotCenterX = dimensions.axisPadding + index * (barSize + spacing) + (barSize + spacing) / 2
        drawContext.canvas.nativeCanvas.drawText(
            label,
            slotCenterX,
            size.height - dimensions.contentPadding,
            paint
        )
    }
}

private fun DrawScope.drawValueLabels(
    maxValue: Float,
    dimensions: GraphDimensions,
    textStyle: GraphStyle.Text
) {
    val steps = 5
    val stepValue = maxValue / steps
    val paint = Paint().asFrameworkPaint().apply {
        color = textStyle.color.toArgb()
        this.textSize = textStyle.size
        textAlign = android.graphics.Paint.Align.RIGHT
    }

    repeat(steps + 1) { i ->
        val value = (stepValue * i).toInt()
        val position = i.toFloat() / steps
        val y = size.height - dimensions.axisPadding -
                position * (size.height - 2 * dimensions.axisPadding)

        drawContext.canvas.nativeCanvas.drawText(
            value.toString(),
            dimensions.axisPadding - 8f,
            y + textStyle.size / 2,
            paint
        )
    }
}