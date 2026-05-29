package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.shikiflow.presentation.common.image.ChapterItem
import kotlinx.coroutines.flow.distinctUntilChanged
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun ChapterScrollModeComponent(
    chapterPageUrls: List<String>,
    chapterPageIndex: Int,
    onPageChange: (Int) -> Unit,
    onNavigationChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = chapterPageIndex
    )
    val zoomState = rememberZoomState()

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                onPageChange(index)
            }
    }

    LaunchedEffect(chapterPageIndex) {
        if(lazyListState.firstVisibleItemIndex != chapterPageIndex) {
            lazyListState.scrollToItem(index = chapterPageIndex)
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .onSizeChanged { newSize ->
                containerSize = newSize
            }
            .zoomable(
                zoomState = zoomState,
                onTap = { offset ->
                    if(offset.x < containerSize.width * 0.35f) {
                        if (chapterPageIndex > 0) {
                            onPageChange(chapterPageIndex - 1)
                        }
                    }
                    else if(offset.x > containerSize.width * 0.65f) {
                        if (chapterPageIndex < chapterPageUrls.size - 1) {
                            onPageChange(chapterPageIndex + 1)
                        }
                    }
                    else {
                        onNavigationChange()
                    }
                },
                onDoubleTap = {
                    zoomState.reset()
                    onNavigationChange()
                }
            ),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(chapterPageUrls.size) { index ->
            ChapterItem(
                pageUrl = chapterPageUrls[index],
                pageNumber = index + 1,
                contentScale = ContentScale.FillWidth
            )
        }
    }
}