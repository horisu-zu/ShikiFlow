package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.R
import com.example.shikiflow.data.mapper.local.TracksMapper.toUserRateData
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.PullToRefreshCustomBox
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.viewmodel.anime.tracks.AnimeTracksViewModel
import com.example.shikiflow.domain.model.settings.AppUiMode
import com.example.shikiflow.domain.model.track.media.MediaTrack
import com.example.shikiflow.utils.PagingUtils.fetched
import com.example.shikiflow.utils.PagingUtils.isLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeTracksPage(
    userStatus: UserRateStatus,
    isCurrentPage: Boolean,
    isAppBarVisible: Boolean,
    onIsAtTopChange: (Boolean) -> Unit,
    onAnimeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tracksViewModel: AnimeTracksViewModel = hiltViewModel(),
) {
    val preferredTitleType = LocalTitleTypeController.current
    val params by tracksViewModel.params.collectAsStateWithLifecycle()
    val animeTrackItems = tracksViewModel.animeTracks[userStatus]?.collectAsLazyPagingItems()
        ?: return
    val appUiMode by tracksViewModel.appUiMode.collectAsStateWithLifecycle()

    var selectedItem by remember { mutableStateOf<MediaTrack?>(null) }

    appUiMode?.let { uiMode ->
        when (animeTrackItems.loadState.refresh) {
            is LoadState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = (animeTrackItems.loadState.refresh as LoadState.Error)
                            .error.message ?: stringResource(R.string.atp_loading_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { animeTrackItems.refresh() }
                    )
                }
            }
            else -> {
                PullToRefreshCustomBox(
                    isRefreshing = false,
                    enabled = isAppBarVisible,
                    onRefresh = { animeTrackItems.refresh() }
                ) {
                    when (uiMode) {
                        AppUiMode.LIST -> {
                            AnimeTracksListComponent(
                                trackItems = animeTrackItems,
                                preferredTitleType = preferredTitleType,
                                isCurrentPage = isCurrentPage,
                                onAnimeClick = onAnimeClick,
                                onLongClick = { item ->
                                    selectedItem = item
                                },
                                onIsAtTopChange = onIsAtTopChange,
                                modifier = modifier
                            )
                        }
                        AppUiMode.GRID -> {
                            AnimeTracksGridComponent(
                                trackItems = animeTrackItems,
                                preferredTitleType = preferredTitleType,
                                isCurrentPage = isCurrentPage,
                                onAnimeClick = onAnimeClick,
                                onLongClick = { item ->
                                    selectedItem = item
                                },
                                onIsAtTopChange = onIsAtTopChange,
                                modifier = modifier
                            )
                        }
                    }
                }

                selectedItem?.let { item ->
                    UserRateBottomSheet(
                        userRate = item.toUserRateData(),
                        preferredTitleType = preferredTitleType,
                        scoreFormat = params.scoreFormat,
                        rateUpdateState = params.rateUpdateState,
                        onDismiss = {
                            if (params.rateUpdateState != RateUpdateState.LOADING) {
                                selectedItem = null
                            }
                        },
                        onSave = { saveUserRate ->
                            tracksViewModel.saveUserRate(saveUserRate)
                        },
                        onDelete = { entryId ->
                            tracksViewModel.deleteUserRate(
                                entryId = entryId,
                                mediaId = item.shortData.id,
                                malId = item.shortData.malId,
                                mediaType = item.shortData.mediaType
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimeTracksListComponent(
    trackItems: LazyPagingItems<MediaTrack>,
    preferredTitleType: PreferredTitleType,
    isCurrentPage: Boolean,
    onAnimeClick: (Int) -> Unit,
    onLongClick: (MediaTrack) -> Unit,
    onIsAtTopChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
            lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(isCurrentPage,isAtTop) {
        if(isCurrentPage) {
            onIsAtTopChange(isAtTop)
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        trackItems.apply {
            if (trackItems.isLoading()) {
                items(count = 12) { index ->
                    AnimeTrackItemPlaceholder(index)
                }
            } else if (trackItems.fetched()) {
                items(
                    count = trackItems.itemCount,
                    key = trackItems.itemKey { it.track.id }
                ) { index ->
                    val item = trackItems[index] ?: return@items

                    AnimeTrackItem(
                        userRate = item,
                        titleType = preferredTitleType,
                        onClick = onAnimeClick,
                        onLongClick = { onLongClick(item) },
                        modifier = Modifier.animateItem()
                    )
                }
            }

            if (loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            } else if (loadState.append is LoadState.Error) {
                item {
                    ErrorItem(
                        message = stringResource(R.string.atp_loading_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        showFace = false,
                        onButtonClick = { trackItems.retry() }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimeTracksGridComponent(
    trackItems: LazyPagingItems<MediaTrack>,
    preferredTitleType: PreferredTitleType,
    isCurrentPage: Boolean,
    onAnimeClick: (Int) -> Unit,
    onLongClick: (MediaTrack) -> Unit,
    onIsAtTopChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyGridState = rememberLazyGridState()
    val isAtTop by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex == 0 &&
            lazyGridState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(isCurrentPage, isAtTop) {
        if(isCurrentPage) {
            onIsAtTopChange(isAtTop)
        }
    }

    LazyVerticalGrid(
        state = lazyGridState,
        columns = GridCells.Adaptive(180.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        trackItems.apply {
            if (trackItems.isLoading()) {
                items(count = 12) {
                    AnimeTrackGridItemPlaceholder()
                }
            } else if (trackItems.fetched()) {
                items(
                    count = trackItems.itemCount,
                    key = trackItems.itemKey { it.track.id }
                ) { index ->
                    val trackItem = trackItems[index] ?: return@items

                    AnimeTrackGridItem(
                        trackItem = trackItem,
                        titleType = preferredTitleType,
                        onClick = onAnimeClick,
                        onLongClick = { onLongClick(trackItem) },
                        modifier = Modifier.animateItem()
                    )
                }
            }

            if (loadState.append is LoadState.Loading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            } else if(loadState.append is LoadState.Error) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ErrorItem(
                        message = stringResource(R.string.atp_loading_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { trackItems.retry() }
                    )
                }
            }
        }
    }
}