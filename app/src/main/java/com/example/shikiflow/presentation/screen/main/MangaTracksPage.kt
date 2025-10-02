package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.manga.MangaTrack
import com.example.shikiflow.domain.model.track.manga.MangaTrack.Companion.toBrowse
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.browse.BrowseGridItem

@Composable
fun MangaTracksPage(
    trackItems: LazyPagingItems<MangaTrack>?,
    onMangaClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    trackItems?.let { items ->
        if(trackItems.loadState.refresh is LoadState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if(trackItems.loadState.refresh is LoadState.Error) {
            ErrorItem(
                message = stringResource(R.string.mtp_loading_error),
                buttonLabel = stringResource(R.string.common_retry),
                onButtonClick = { trackItems.refresh() }
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    count = trackItems.itemCount,
                    key = trackItems.itemKey { it.track.id }
                ) { index ->
                    trackItems[index]?.toBrowse()?.let { mangaItem ->
                        BrowseGridItem(
                            browseItem = mangaItem,
                            onItemClick = { id, mediaType ->
                                onMangaClick(id)
                            }
                        )
                    }
                }
                trackItems.apply {
                    if(loadState.append is LoadState.Loading) {
                        item(span = { GridItemSpan(3) }) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                    }
                    if(loadState.append is LoadState.Error) {
                        item(span = { GridItemSpan(3) }) {
                            ErrorItem(
                                message = stringResource(R.string.mtp_loading_error),
                                buttonLabel = stringResource(R.string.common_retry),
                                showFace = false,
                                onButtonClick = { trackItems.retry() }
                            )
                        }
                    }
                }
            }
        }
    }
}