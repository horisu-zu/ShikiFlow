package com.example.shikiflow.presentation.common

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun Modifier.shimmerEffect(): Modifier {
    val colors = listOf(
        colorScheme.surfaceContainerHighest,
        colorScheme.surfaceContainerHigh,
        colorScheme.surfaceContainerLow,
        colorScheme.surfaceContainerHigh,
        colorScheme.surfaceContainerHighest,
    )

    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    return drawBehind {
        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(startOffsetX, 0f),
                end = Offset(startOffsetX + size.width, size.height)
            )
        )
    }
}

fun Modifier.systemBarsVisibility(visible: Boolean): Modifier = composed {
    val activity = LocalActivity.current
    val window = activity?.window

    DisposableEffect(visible) {
        window?.let { w ->
            val controller = WindowCompat.getInsetsController(w, w.decorView)
            if (visible) {
                controller.show(WindowInsetsCompat.Type.systemBars())
            } else {
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }

        onDispose {
            window?.let { w ->
                val controller = WindowCompat.getInsetsController(w, w.decorView)
                controller.show(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        }
    }

    this@systemBarsVisibility
}

fun Modifier.ignoreHorizontalParentPadding(horizontal: Dp): Modifier {
    return this.layout { measurable, constraints ->
        val overridenWidth = constraints.maxWidth + 2 * horizontal.roundToPx()
        val placeable = measurable.measure(constraints.copy(maxWidth = overridenWidth))

        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}

fun Modifier.foregroundGradient(
    gradientColors: List<Color>,
    startY: Float = 0f,
    gradientFraction: Float = 1f
) = this.drawWithCache {
    onDrawWithContent {
        drawContent()
        drawRect(
            brush = Brush.verticalGradient(
                colors = gradientColors,
                startY = startY,
                endY = size.height * gradientFraction
            )
        )
    }
}