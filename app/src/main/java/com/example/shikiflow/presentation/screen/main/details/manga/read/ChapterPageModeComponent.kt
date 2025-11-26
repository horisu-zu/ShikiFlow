package com.example.shikiflow.presentation.screen.main.details.manga.read

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.imageLoader
import coil3.request.ImageRequest
import com.example.shikiflow.presentation.common.image.ChapterItem
import com.example.shikiflow.presentation.viewmodel.manga.read.ChapterNavigationViewModel
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun ChapterPageModeComponent(
    chapterPageUrls: List<String>,
    chapterPage: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    chapterNavigationViewModel: ChapterNavigationViewModel = hiltViewModel()
) {
    val pageCount = chapterPageUrls.size
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val imageLoader = remember { context.imageLoader }
    val isNavigationVisible by chapterNavigationViewModel.isNavigationVisible.collectAsStateWithLifecycle()
    val isScrolling by remember {
        derivedStateOf {
            scrollState.isScrollInProgress
        }
    }

    LaunchedEffect(chapterPage) {
        scrollState.scrollTo(0)
        chapterNavigationViewModel.onScrollDetected()

        if (chapterPage < chapterPageUrls.size) {
            val nextPageUrl = chapterPageUrls[chapterPage]
            Log.d("ChapterPageModeComponent", "Loading next page: $nextPageUrl")
            imageLoader.enqueue(
                ImageRequest.Builder(context)
                    .data(nextPageUrl)
                    .memoryCacheKey(nextPageUrl)
                    .build()
            )
        }
    }

    LaunchedEffect(isScrolling) {
        if(isScrolling) {
            chapterNavigationViewModel.onScrollDetected()
        }
    }

    Box(modifier = modifier) {
        ChapterItem(
            pageUrl = chapterPageUrls[chapterPage - 1],
            pageNumber = chapterPage,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxSize()
                .verticalScroll(scrollState)
                .onSizeChanged { newSize ->
                    containerSize = newSize
                }
                .zoomable(
                    zoomState = rememberZoomState(),
                    onTap = { offset ->
                        if (offset.x < containerSize.width / 2) {
                            if (chapterPage > 1) {
                                onPageChange(chapterPage - 1)
                            }
                        } else {
                            if (chapterPage < pageCount) {
                                onPageChange(chapterPage + 1)
                            }
                        }
                    }
                ) // Integrated library works much better (I have some issues with pointerInput)
            /*.pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    val newScale = (scale * zoom).coerceIn(1f, 3f)
                    scale = newScale

                    if (newScale > 1f) {
                        val maxOffsetX = (newScale - 1f) * 200f
                        val maxOffsetY = (newScale - 1f) * 200f
                        offsetX = (offsetX + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                        offsetY = (offsetY + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                    }
                }
                detectTapGestures { offset ->
                    if (offset.x < size.width / 2) {
                        if (currentPage > 1) {
                            currentPage--
                        }
                    } else {
                        if (currentPage < pageCount) {
                            currentPage++
                        }
                    }
                }
        }*/
        )
        AnimatedVisibility(
            visible = isNavigationVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .imePadding()
        ) {
            ChapterNavigationComponent(
                currentPage = chapterPage,
                pageCount = pageCount,
                onNavigateClick = { pageNumber ->
                    onPageChange(pageNumber.coerceIn(1, pageCount))
                },
                onInteractionStart = chapterNavigationViewModel::onUserInteractionStart,
                onInteractionEnd = chapterNavigationViewModel::onUserInteractionEnd
            )
        }
    }
}
