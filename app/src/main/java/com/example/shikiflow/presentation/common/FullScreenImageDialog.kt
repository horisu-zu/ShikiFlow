package com.example.shikiflow.presentation.common

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import mx.platacard.pagerindicator.PagerIndicatorOrientation
import mx.platacard.pagerindicator.PagerWormIndicator
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FullScreenImageDialog(
    imageUrls: List<String>,
    initialIndex: Int,
    visibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    imageType: ImageType = ImageType.Screenshot(),
) {
    BackHandler {
        onDismiss()
    }

    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { imageUrls.size }
    )

    Box(modifier = modifier.background(Color.Black.copy(alpha = 0.7f))) {
        HorizontalPager(state = pagerState) { pageIndex ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                BaseImage(
                    model = imageUrls[pageIndex],
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth(0.8f)
                        .zoomable(rememberZoomState())
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = imageUrls[pageIndex]),
                            animatedVisibilityScope = visibilityScope
                        ),
                    imageType = imageType
                )
            }
        }
        if (imageUrls.size > 1) {
            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(all = 32.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                PagerWormIndicator(
                    dotCount = 5,
                    pagerState = pagerState,
                    activeDotColor = Color.White,
                    dotColor = Color.White.copy(alpha = 0.3f),
                    activeDotSize = 12.dp,
                    minDotSize = 6.dp,
                    orientation = PagerIndicatorOrientation.Horizontal
                )
            }
        }
    }
}

/*
private enum class IndicatorState {
    START, MIDDLE, END
}

@Composable
private fun PagerIndicator(
    currentIndex: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
    dotSize: Dp = 12.dp,
    spacing: Dp = 6.dp,
    activeAlpha: Float = 1f,
    inactiveAlpha: Float = 0.3f,
    animationDuration: Int = 300,
    color: Color = Color.White
) {
    val indicatorState = when (currentIndex) {
        0 -> IndicatorState.START
        totalCount - 1 -> IndicatorState.END
        else -> IndicatorState.MIDDLE
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IndicatorState.entries.forEach { dotState ->
            val alpha by animateFloatAsState(
                targetValue = if (indicatorState == dotState) activeAlpha else inactiveAlpha,
                animationSpec = tween(animationDuration)
            )

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(color.copy(alpha = alpha))
            )
        }
    }
}*/
