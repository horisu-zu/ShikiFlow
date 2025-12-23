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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.manga.MangaTrack.Companion.toBrowse
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.PullToRefreshCustomBox
import com.example.shikiflow.presentation.screen.browse.BrowseGridItem
import com.example.shikiflow.presentation.viewmodel.manga.MangaTracksViewModel

@Composable
fun MangaTracksPage(
    userStatus: UserRateStatusEnum,
    isAppBarVisible: Boolean,
    onMangaClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    mangaTracksViewModel: MangaTracksViewModel = hiltViewModel()
) {
    val mangaTrackItems = mangaTracksViewModel.getMangaTracks(userStatus).collectAsLazyPagingItems()
    var isRefreshing by remember { mutableStateOf(false) }

    PullToRefreshCustomBox(
        isRefreshing = isRefreshing,
        enabled = isAppBarVisible,
        onRefresh = {
            isRefreshing = true
            mangaTrackItems.refresh()
            isRefreshing = false
        }
    ) {
        if(mangaTrackItems.loadState.refresh is LoadState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if(mangaTrackItems.loadState.refresh is LoadState.Error) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = stringResource(R.string.mtp_loading_error),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { mangaTrackItems.refresh() }
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(120.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier.fillMaxSize()
            ) {
                items(
                    count = mangaTrackItems.itemCount,
                    key = mangaTrackItems.itemKey { it.track.id }
                ) { index ->
                    mangaTrackItems[index]?.toBrowse()?.let { mangaItem ->
                        BrowseGridItem(
                            browseItem = mangaItem,
                            onItemClick = { id, mediaType ->
                                onMangaClick(id)
                            }
                        )
                    }
                }
                mangaTrackItems.apply {
                    if(loadState.append is LoadState.Loading) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                    }
                    if(loadState.append is LoadState.Error) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            ErrorItem(
                                message = stringResource(R.string.mtp_loading_error),
                                buttonLabel = stringResource(R.string.common_retry),
                                showFace = false,
                                onButtonClick = { mangaTrackItems.retry() }
                            )
                        }
                    }
                }
            }
        }
    }
}