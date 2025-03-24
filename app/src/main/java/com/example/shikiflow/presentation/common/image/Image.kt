package com.example.shikiflow.presentation.common.image

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun BaseImage(
    model: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
    imageType: ImageType = ImageType.Poster()
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(model)
            .memoryCacheKey(model)
            .diskCacheKey(model)
            .listener(
                onSuccess = { _ , result ->
                    Log.d("Image", "Image successfully loaded: $result")
                },
                onError = { _ , error ->
                    Log.d("Image", "Error loading image: $error")
                }
            )
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
            .width(imageType.defaultWidth)
            .aspectRatio(imageType.defaultAspectRatio)
            .clip(imageType.defaultClip),
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .shimmerEffect()
            )
        }
    )
}

@Composable
fun GradientImage(
    model: String?,
    modifier: Modifier = Modifier,
    imageType: ImageType = ImageType.Poster(),
    gradientFraction: Float = 0.3f,
    gradientColors: List<Color> = listOf(
        Color.Transparent,
        MaterialTheme.colorScheme.background
    ),
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .clip(imageType.defaultClip)
    ) {
        BaseImage(
            model = model,
            contentScale = contentScale,
            contentDescription = contentDescription,
            imageType = imageType,
            modifier = Modifier.fillMaxWidth()
        )

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

/*@Composable
fun CoverImage(
    model: String?,
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    alpha: Float = 0.2f,
    clip: RoundedCornerShape = RoundedCornerShape(12.dp),
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxSize()) {
            GradientImage(
                model = model,
                contentScale = ContentScale.Fit,
                gradientFraction = 0.8f,
                contentDescription = null,
                modifier = Modifier
                    .alpha(alpha)
            )

            BaseImage(
                model = model,
                contentScale = contentScale,
                contentDescription = contentDescription,
                modifier = Modifier
                    .width(size)
                    .clip(clip)
                    .align(Alignment.Center)
            )
        }
    }
}*/

@Composable
fun RoundedImage(
    model: String?,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    clip: RoundedCornerShape = CircleShape,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null
) {
    AsyncImage(
        model = model,
        modifier = modifier
            .size(size)
            .clip(clip),
        contentScale = contentScale,
        contentDescription = contentDescription
    )
}