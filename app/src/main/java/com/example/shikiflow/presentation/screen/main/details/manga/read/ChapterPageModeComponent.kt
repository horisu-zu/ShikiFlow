package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.shikiflow.presentation.common.image.ChapterItem
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import kotlin.math.abs

@Composable
fun ChapterPageModeComponent(
    chapterPageUrls: List<String>,
    chapterPageIndex: Int,
    onPageChange: (Int) -> Unit,
    onScrollDetected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pageCount = chapterPageUrls.size
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    val pagerState = rememberPagerState(
        initialPage = chapterPageIndex,
        pageCount = { pageCount }
    )

    LaunchedEffect(chapterPageIndex) {
        val pageDifference = pagerState.currentPage - (chapterPageIndex)

        if (abs(pageDifference) == 1) {
            pagerState.animateScrollToPage(chapterPageIndex)
        } else if(abs(pageDifference) > 1) {
            pagerState.scrollToPage(chapterPageIndex)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .filter { !pagerState.isScrollInProgress || pagerState.targetPage == pagerState.currentPage }
            .collect { page ->
                onPageChange(page)
            }
    }

    HorizontalPager(
        state = pagerState,
        beyondViewportPageCount = 1,
        modifier = modifier
    ) { page ->
        val scrollState = rememberScrollState()

        LaunchedEffect(scrollState) {
            snapshotFlow { scrollState.scrollIndicatorState?.scrollOffset }
                .filter { it != 0 }
                .distinctUntilChanged()
                .collect {
                    onScrollDetected()
                }
        }

        ChapterItem(
            pageUrl = chapterPageUrls[page],
            pageNumber = chapterPageIndex + 1,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .onSizeChanged { newSize ->
                    containerSize = newSize
                }
                .zoomable(
                    zoomState = rememberZoomState(),
                    onTap = { offset ->
                        if(offset.x < containerSize.width * 0.35f) {
                            if (chapterPageIndex > 0) {
                                onPageChange(chapterPageIndex - 1)
                            }
                        }
                        else if(offset.x > containerSize.width * 0.65f) {
                            if (chapterPageIndex < pageCount - 1) {
                                onPageChange(chapterPageIndex + 1)
                            }
                        }
                        else {
                            onScrollDetected()
                        }
                    },
                    onDoubleTap = {
                        onScrollDetected()
                    }
                )
        )
    }
}
