package com.example.shikiflow.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.launch
import kotlin.math.abs

fun Modifier.stretchOverscroll(): Modifier = composed {
    val overscrollOffset = remember { Animatable(0f) }
    val overscrollThreshold = 100f
    val scope = rememberCoroutineScope()
    val scale = remember {
        derivedStateOf {
            1f + (abs(overscrollOffset.value) / overscrollThreshold) * 0.02f
        }
    }

    this.nestedScroll(
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val newOffset = overscrollOffset.value + available.y
                scope.launch {
                    overscrollOffset.snapTo(newOffset.coerceIn(-overscrollThreshold, overscrollThreshold))
                }
                return super.onPostScroll(consumed, available, source)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                overscrollOffset.animateTo(0f, animationSpec = tween(durationMillis = 300))
                return super.onPostFling(consumed, available)
            }
        }
    ).graphicsLayer {
        scaleY = scale.value
    }
}