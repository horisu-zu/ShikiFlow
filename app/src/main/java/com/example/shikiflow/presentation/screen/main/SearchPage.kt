package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.shikiflow.R
import com.example.shikiflow.data.mapper.local.TracksMapper.toUserRateData
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.media.MediaTrack
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.PullToRefreshCustomBox
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.UserRateBottomSheet
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper.mapStatus
import com.example.shikiflow.presentation.viewmodel.anime.tracks.search.TracksSearchViewModel

@Composable
fun SearchPage(
    searchQuery: String,
    isAppBarVisible: Boolean,
    mediaType: MediaType,
    onMediaClick: (MediaType, Int) -> Unit,
    modifier: Modifier = Modifier,
    tracksViewModel: TracksSearchViewModel = hiltViewModel()
) {
    val horizontalPadding = 12.dp
    val chips = listOf(null) + UserRateStatus.entries.filter { it != UserRateStatus.UNKNOWN }.toList()
    var selectedTabSearch by rememberSaveable { mutableIntStateOf(0) }
    var selectedItem by remember { mutableStateOf<MediaTrack?>(null) }

    val trackItems = tracksViewModel.animeTracksItems.collectAsLazyPagingItems()
    val rateUpdateState by tracksViewModel.rateUpdateState.collectAsStateWithLifecycle()

    LaunchedEffect(mediaType) {
        tracksViewModel.setMediaType(mediaType)
    }

    LaunchedEffect(selectedTabSearch) {
        tracksViewModel.setRateStatus(
            userRateStatus = when (selectedTabSearch) {
                in 1..UserRateStatus.entries.size -> UserRateStatus.entries[selectedTabSearch - 1]
                else -> null
            }
        )
    }

    LaunchedEffect(searchQuery) {
        tracksViewModel.setQuery(searchQuery)
    }

    Column {
        SnapFlingLazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    shape = RoundedCornerShape(
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chips) { rateStatus ->
                val userRateStatus = rateStatus?.let {
                    rateStatus.mapStatus(mediaType)
                } ?: R.string.media_user_status_all

                FilterChip(
                    selected = chips[selectedTabSearch] == rateStatus,
                    onClick = { selectedTabSearch = chips.indexOf(rateStatus) },
                    label = {
                        Text(
                            text = stringResource(id = userRateStatus)
                        )
                    },
                    leadingIcon = if (chips[selectedTabSearch] == rateStatus) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else { null },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        }

        if(trackItems.loadState.refresh is LoadState.Error) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = stringResource(R.string.common_error),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { trackItems.retry() }
                )
            }
        } else {
            PullToRefreshCustomBox(
                isRefreshing = trackItems.loadState.refresh is LoadState.Loading,
                onRefresh = { trackItems.refresh() },
                enabled = isAppBarVisible,
                modifier = modifier
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(120.dp),
                    contentPadding = PaddingValues(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = 12.dp,
                        bottom = WindowInsets.navigationBars
                            .asPaddingValues()
                            .calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    items(
                        count = trackItems.itemCount,
                        key = trackItems.itemKey { it.shortData.id }
                    ) { index ->
                        val item = trackItems[index] ?: return@items

                        when(item.shortData.mediaType) {
                            MediaType.ANIME -> {
                                AnimeTrackGridItem(
                                    trackItem = item,
                                    onClick = { id -> onMediaClick(mediaType, id) },
                                    onLongClick = { trackItem ->
                                        selectedItem = trackItem
                                    },
                                    modifier = Modifier.animateItem()
                                )
                            }
                            MediaType.MANGA -> {
                                MangaTrackItem(
                                    trackItem = item,
                                    onClick = { id -> onMediaClick(mediaType, id) },
                                    onLongClick = { trackItem ->
                                        selectedItem = trackItem
                                    },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                    trackItems.apply {
                        when {
                            loadState.append is LoadState.Error -> {
                                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ErrorItem(
                                            message = stringResource(R.string.common_error),
                                            buttonLabel = stringResource(R.string.common_retry),
                                            onButtonClick = { trackItems.retry() }
                                        )
                                    }
                                }
                            }
                            loadState.append is LoadState.Loading -> {
                                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) { CircularProgressIndicator() }
                                }
                            }
                        }
                    }
                }

                selectedItem?.let {
                    UserRateBottomSheet(
                        userRate = it.toUserRateData(),
                        rateUpdateState = rateUpdateState,
                        onDismiss = {
                            if (rateUpdateState != RateUpdateState.LOADING) {
                                selectedItem = null
                            }
                        },
                        onSave = { saveUserRate ->
                            tracksViewModel.saveUserRate(saveUserRate)
                        }
                    )
                }
            }
        }
    }
}