package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.R
import com.example.shikiflow.data.mapper.local.TracksMapper.toUserRateData
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.PullToRefreshCustomBox
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.viewmodel.anime.tracks.AnimeTracksViewModel
import com.example.shikiflow.domain.model.settings.AppUiMode
import com.example.shikiflow.domain.model.track.media.MediaTrack
import com.example.shikiflow.presentation.common.CustomDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeTracksPage(
    userStatus: UserRateStatus,
    isAppBarVisible: Boolean,
    onAnimeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tracksViewModel: AnimeTracksViewModel = hiltViewModel(),
) {
    val params by tracksViewModel.params.collectAsStateWithLifecycle()
    val animeTrackItems = tracksViewModel.animeTracks[userStatus]?.collectAsLazyPagingItems()
        ?: return
    val appUiMode by tracksViewModel.appUiMode.collectAsStateWithLifecycle()

    var selectedItem by remember { mutableStateOf<MediaTrack?>(null) }
    var deleteEntryId by remember { mutableStateOf<Int?>(null) }

    when (animeTrackItems.loadState.refresh) {
        is LoadState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }
        is LoadState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = stringResource(R.string.atp_loading_error),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { animeTrackItems.refresh() }
                )
            }
        }
        else -> {
            PullToRefreshCustomBox(
                isRefreshing = animeTrackItems.loadState.refresh is LoadState.Loading,
                enabled = isAppBarVisible,
                onRefresh = { animeTrackItems.refresh() }
            ) {
                when (appUiMode) {
                    AppUiMode.LIST -> {
                        AnimeTracksListComponent(
                            trackItems = animeTrackItems,
                            onAnimeClick = onAnimeClick,
                            onLongClick = { item ->
                                selectedItem = item
                            },
                            modifier = modifier
                        )
                    }
                    AppUiMode.GRID -> {
                        AnimeTracksGridComponent(
                            trackItems = animeTrackItems,
                            onAnimeClick = onAnimeClick,
                            onLongClick = { item ->
                                selectedItem = item
                            },
                            modifier = modifier
                        )
                    }
                }
            }

            selectedItem?.let { item ->
                UserRateBottomSheet(
                    userRate = item.toUserRateData(),
                    rateUpdateState = params.rateUpdateState,
                    onDismiss = {
                        if (params.rateUpdateState != RateUpdateState.LOADING) {
                            selectedItem = null
                        }
                    },
                    onSave = { saveUserRate ->
                        tracksViewModel.saveUserRate(saveUserRate)
                    },
                    onDelete = { deleteEntryId = it }
                )

                deleteEntryId?.let { entryId ->
                    CustomDialog(
                        onDismissRequest = { deleteEntryId = null },
                        text = stringResource(R.string.user_rate_delete),
                        confirmButtonText = stringResource(R.string.common_ok),
                        onConfirm = {
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
    onAnimeClick: (Int) -> Unit,
    onLongClick: (MediaTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 12.dp,
            vertical = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            count = trackItems.itemCount,
            key = trackItems.itemKey { it.track.id }
        ) { index ->
            val item = trackItems[index] ?: return@items

            AnimeTrackItem(
                userRate = item,
                onClick = onAnimeClick,
                onLongClick = { onLongClick(item) },
                modifier = Modifier.animateItem()
            )
        }
        trackItems.apply {
            if(loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }
            if(loadState.append is LoadState.Error) {
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
    onAnimeClick: (Int) -> Unit,
    onLongClick: (MediaTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            count = trackItems.itemCount,
            key = trackItems.itemKey { it.track.id }
        ) { index ->
            val trackItem = trackItems[index] ?: return@items

            AnimeTrackGridItem(
                trackItem = trackItem,
                onClick = onAnimeClick,
                onLongClick = { onLongClick(trackItem) },
                modifier = Modifier.animateItem()
            )
        }
        trackItems.apply {
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
                        message = stringResource(R.string.atp_loading_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { trackItems.retry() }
                    )
                }
            }
        }
    }
}