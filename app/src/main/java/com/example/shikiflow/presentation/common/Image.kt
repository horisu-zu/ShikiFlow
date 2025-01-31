package com.example.shikiflow.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun Image(
    model: String?,
    modifier: Modifier = Modifier,
    clip: Dp = 12.dp,
    gradientFraction: Float? = null,
    gradientColors: List<Color> = listOf(
        Color.Transparent,
        MaterialTheme.colorScheme.background
    ),
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null
) {
    var aspectRatio by remember { mutableStateOf(1f) }
    //var imageHeightPx by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(clip))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model)
                .crossfade(true)
                .listener(
                    onSuccess = { _, result ->
                        val width = result.image.width
                        val height = result.image.height
                        aspectRatio = width.toFloat() / height.toFloat()
                    }
                )
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.aspectRatio(aspectRatio)
        )

        if (gradientFraction != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawWithCache {
                        val brush = Brush.verticalGradient(
                            colors = gradientColors,
                            startY = 0f,
                            endY = size.height * gradientFraction
                        )
                        onDrawBehind {
                            drawRect(brush)
                        }
                    }
            )
        }
    }
}

@Composable
fun CircularImage(
    model: String?,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null
) {
    AsyncImage(
        model = model,
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentScale = contentScale,
        contentDescription = contentDescription
    )
}