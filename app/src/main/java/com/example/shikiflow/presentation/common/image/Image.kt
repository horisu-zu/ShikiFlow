package com.example.shikiflow.presentation.common.image

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import coil3.imageLoader
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import com.example.shikiflow.R

@Composable
fun BaseImage(
    model: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
    imageType: ImageType = ImageType.Poster(),
    error: @Composable () -> Unit = {}
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(model)
            .memoryCacheKey(model)
            .diskCacheKey(model)
            .listener(
                onSuccess = { _ , result ->
                    //Log.d("Image", "Image successfully loaded: $result")
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
        },
        error = { error() }
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
    BaseImage(
        model = model,
        modifier = modifier
            .size(size)
            .clip(clip),
        contentScale = contentScale,
        contentDescription = contentDescription
    )
}

@Composable
fun ChapterItem(
    pageUrl: String,
    pageNumber: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val context = LocalContext.current
    var retryKey by remember { mutableIntStateOf(0) }
    val imageLoader = context.imageLoader

    key(retryKey) {
        val imageRequest = remember {
            ImageRequest.Builder(context)
                .data(pageUrl)
                .allowHardware(false)
                .memoryCacheKey(pageUrl)
                .crossfade(true)
                .build()
        }

        SubcomposeAsyncImage(
            model = imageRequest,
            contentDescription = pageUrl,
            contentScale = contentScale,
            imageLoader = imageLoader,
            modifier = modifier.fillMaxWidth(),
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.85f),
                    contentAlignment = Alignment.Center
                ) { Text(
                    text = pageNumber.toString(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                    )
                ) }
            },
            error = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.85f),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = pageNumber.toString(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                        )
                    )
                    Button(
                        onClick = {
                            imageLoader.memoryCache?.remove(MemoryCache.Key(pageUrl))
                            retryKey++
                        },
                        modifier = Modifier
                            .wrapContentWidth()
                            .clip(CircleShape)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retry",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = stringResource(id = R.string.common_retry),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        )
    }
}