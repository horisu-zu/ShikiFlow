package com.example.shikiflow.presentation.screen.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper.mapStatus
import com.example.shikiflow.presentation.viewmodel.anime.tracks.search.AnimeTracksSearchViewModel

@Composable
fun SearchPage(
    userId: Int?,
    searchQuery: String,
    isAtTop: Boolean,
    tracksViewModel: AnimeTracksSearchViewModel = hiltViewModel(),
    onAnimeClick: (Int) -> Unit
) {
    val chips = listOf(null) + UserRateStatus.entries.filter { it != UserRateStatus.UNKNOWN }.toList()
    var selectedTabSearch by rememberSaveable { mutableIntStateOf(0) }

    val trackItems = tracksViewModel.animeTracksItems.collectAsLazyPagingItems()

    LaunchedEffect(userId) {
        userId?.let {
            tracksViewModel.setUserId(userId)
        }
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

    LaunchedEffect(trackItems) {
        Log.d("SearchPage", "Track Items State: ${trackItems.loadState}")
    }

    Column {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if(isAtTop) MaterialTheme.colorScheme.background
                        else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chips) { rateStatus ->
                val userRateStatus = rateStatus?.let {
                    rateStatus.mapStatus(MediaType.ANIME)
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
                    } else { null }
                )
            }
        }

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (trackItems.loadState.refresh) {
                is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                }
                is LoadState.Error -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorItem(
                                message = stringResource(R.string.atp_loading_error),
                                buttonLabel = stringResource(R.string.common_retry),
                                onButtonClick = { trackItems.refresh() }
                            )
                        }
                    }
                }
                else -> {
                    items(
                        count = trackItems.itemCount,
                        key = trackItems.itemKey { it.anime.id }
                    ) { index ->
                        val item = trackItems[index] ?: return@items
                        SearchAnimeTrackItem(
                            animeItem = item,
                            onItemClick = { id -> onAnimeClick(id) }
                        )
                    }
                    trackItems.apply {
                        when {
                            loadState.append is LoadState.Error -> {
                                item {
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
                                item {
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
            }
        }
    }
}