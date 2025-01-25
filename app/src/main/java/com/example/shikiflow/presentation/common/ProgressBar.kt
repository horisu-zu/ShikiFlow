package com.example.shikiflow.presentation.common

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    cornerRadius: Dp = 8.dp,
    height: Dp = 4.dp
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(cornerRadius)),
        color = progressColor,
        trackColor = backgroundColor,
        drawStopIndicator = {}
    )
}