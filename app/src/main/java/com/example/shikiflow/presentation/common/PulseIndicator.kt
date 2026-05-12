package com.example.shikiflow.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun PulseIndicator(
    backgroundColor: Color,
    itemColor: Color,
    modifier: Modifier = Modifier,
    periodMs: Long = 3600L,
    offsetCount: Int = 2
) {
    val offsetsMs = LongArray(offsetCount) { index ->
        (periodMs / offsetCount) * index
    }
    val startNs = remember { System.nanoTime() }
    var frameTimeNs by remember { mutableLongStateOf(startNs) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { now -> frameTimeNs = now }
        }
    }

    fun phase(offsetMs: Long): Float {
        val elapsedMs = (frameTimeNs - startNs) / 1_000_000L + offsetMs
        return ((elapsedMs % periodMs).toFloat() / periodMs.toFloat())
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        @Composable
        fun Ring(p: Float) = Box(
            Modifier
                .matchParentSize()
                .graphicsLayer {
                    scaleX = 1f + 0.8f * p
                    scaleY = 1f + 0.8f * p
                    alpha = 1f - p
                }
                .border(1.5.dp, itemColor.copy(alpha = 0.9f), CircleShape)
        )

        repeat(offsetsMs.size) { index ->
            Ring(p = phase(offsetsMs[index]))
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(backgroundColor, CircleShape)
                .padding(all = 8.dp)
                .background(itemColor, CircleShape)
        )
    }
}