package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.anime.MyListString
import com.example.shikiflow.domain.model.mapper.UserRateStatusConstants
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.viewmodel.anime.AnimeTracksSearchViewModel

@Composable
fun SearchPage(
    searchQuery: String,
    isAtTop: Boolean,
    tracksViewModel: AnimeTracksSearchViewModel = hiltViewModel(),
    onAnimeClick: (String) -> Unit
) {
    val chips = listOf(R.string.media_user_status_all) +
        UserRateStatusConstants.getStatusChips(MediaType.ANIME)

    var selectedTabSearch by remember { mutableIntStateOf(0) }
    val selectedStatus = when (selectedTabSearch) {
        in 1..chips.size -> MyListString.entries[selectedTabSearch - 1]
        else -> null
    }

    val trackItems = tracksViewModel.getPaginatedTracks(
        title = searchQuery,
        userStatus = selectedStatus
    ).collectAsLazyPagingItems()

    Column {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if(isAtTop) MaterialTheme.colorScheme.background
                        else MaterialTheme.colorScheme.surface
                ),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chips) { tab ->
                FilterChip(
                    selected = chips[selectedTabSearch] == tab,
                    onClick = { selectedTabSearch = chips.indexOf(tab) },
                    label = { Text(stringResource(id = tab)) },
                    leadingIcon = if (chips[selectedTabSearch] == tab) {
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
            if(trackItems.loadState.refresh is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            } else if(trackItems.loadState.refresh is LoadState.Error) {
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
            } else {
                items(
                    count = trackItems.itemCount,
                    key = trackItems.itemKey { it.id }
                ) { index ->
                    val item = trackItems[index] ?: return@items
                    SearchAnimeTrackItem(
                        animeItem = item,
                        onItemClick = { id -> onAnimeClick(id) }
                    )
                }
            }
        }
    }
}