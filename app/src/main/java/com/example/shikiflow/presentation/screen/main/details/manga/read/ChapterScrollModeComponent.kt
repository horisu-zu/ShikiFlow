package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
    onScrollDetected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = chapterPageIndex
    )

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                onPageChange(index)
            }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.firstVisibleItemIndex to
            lazyListState.firstVisibleItemScrollOffset
        }
            .distinctUntilChanged()
            .collect {
                onScrollDetected()
            }
    }

    LaunchedEffect(chapterPageIndex) {
        if(lazyListState.firstVisibleItemIndex != chapterPageIndex) {
            lazyListState.scrollToItem(index = chapterPageIndex)
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier.zoomable(rememberZoomState()),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        )
    ) {
        items(chapterPageUrls.size) { index ->
            val chapterPageUrl = chapterPageUrls[index]

            ChapterItem(
                pageUrl = chapterPageUrl,
                pageNumber = index + 1,
                contentScale = ContentScale.FillWidth
            )
        }
    }
}