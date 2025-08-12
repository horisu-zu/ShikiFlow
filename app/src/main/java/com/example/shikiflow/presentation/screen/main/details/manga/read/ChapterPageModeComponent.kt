package com.example.shikiflow.presentation.screen.main.details.manga.read

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.imageLoader
import coil3.request.ImageRequest
import com.example.shikiflow.presentation.common.image.ChapterItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun ChapterPageModeComponent(
    chapterPageUrls: List<String>,
    modifier: Modifier = Modifier
) {
    var currentPage by rememberSaveable { mutableIntStateOf(1) }
    val pageCount = chapterPageUrls.size

    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    val context = LocalContext.current
    val imageLoader = remember { context.imageLoader }

    LaunchedEffect(currentPage) {
        withContext(Dispatchers.IO) {
            if (currentPage < chapterPageUrls.size) {
                val nextPageUrl = chapterPageUrls[currentPage]
                Log.d("ChapterPageModeComponent", "Loading next page: $nextPageUrl")
                imageLoader.enqueue(
                    ImageRequest.Builder(context)
                        .data(nextPageUrl)
                        .memoryCacheKey(nextPageUrl)
                        .build()
                )
            }
        }
    }

    Column(
        modifier = modifier.padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ChapterItem(
            pageUrl = chapterPageUrls[currentPage - 1],
            pageNumber = currentPage,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.weight(1f)
                .onSizeChanged { newSize ->
                    containerSize = newSize
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                ).zoomable(
                    rememberZoomState(),
                    onTap = { offset ->
                        if (offset.x < containerSize.width / 2) {
                            if (currentPage > 1) {
                                currentPage--
                            }
                        } else {
                            if (currentPage < pageCount) {
                                currentPage++
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
        ChapterNavigationComponent(
            modifier = Modifier.fillMaxWidth().imePadding(),
            currentPage = currentPage,
            pageCount = pageCount,
            onNavigateClick = { pageNumber ->
                currentPage = pageNumber.coerceIn(1, pageCount)
            }
        )
    }
}
