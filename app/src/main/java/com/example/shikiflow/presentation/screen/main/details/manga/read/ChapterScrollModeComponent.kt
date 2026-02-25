package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
    chapterPage: Int,
    onPageChange: (Int) -> Unit,
    onScrollDetected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = chapterPage - 1
    )

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                onPageChange(index + 1)
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

    LaunchedEffect(chapterPage) {
        val pageIndex = chapterPage - 1

        if(lazyListState.firstVisibleItemIndex != pageIndex) {
            lazyListState.scrollToItem(index = pageIndex)
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier.zoomable(rememberZoomState()),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(
            bottom = 12.dp
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