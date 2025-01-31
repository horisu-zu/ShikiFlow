package com.example.shikiflow.presentation.screen.main

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shikiflow.data.anime.MyListString
import com.example.shikiflow.presentation.viewmodel.anime.AnimeTracksSearchViewModel
import com.example.shikiflow.presentation.viewmodel.SearchViewModel

@Composable
fun SearchPage(
    searchViewModel: SearchViewModel = hiltViewModel(),
    tracksViewModel: AnimeTracksSearchViewModel = hiltViewModel()
) {
    val searchQuery by searchViewModel.screenState.collectAsState()
    val searchResults by tracksViewModel.searchResults.collectAsState()
    val isSearching by tracksViewModel.isSearching.collectAsState()
    val hasMorePages by tracksViewModel.searchHasMorePages.collectAsState()
    val chips = listOf("All", "Watching", "Planned", "Watched", "Rewatching", "On Hold", "Dropped")

    var selectedTabSearch by remember { mutableStateOf(0) }

    val selectedStatus = when (chips[selectedTabSearch]) {
        "Watching" -> MyListString.WATCHING
        "Planned" -> MyListString.PLANNED
        "Rewatching" -> MyListString.REWATCHING
        "Watched" -> MyListString.COMPLETED
        "On Hold" -> MyListString.ON_HOLD
        "Dropped" -> MyListString.DROPPED
        else -> null
    }

    val shouldLoadMore = remember {
        derivedStateOf {
            hasMorePages[selectedStatus] == true && isSearching[selectedStatus] != true
        }
    }

    LaunchedEffect(searchQuery.query, selectedTabSearch) {
        if (searchQuery.query.isNotEmpty()) {
            tracksViewModel.searchAnimeTracks(
                name = searchQuery.query,
                status = selectedStatus
            )
        } else {
            tracksViewModel.clearSearchResults()
        }
    }

    Column {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chips) { tab ->
                FilterChip(
                    selected = chips[selectedTabSearch] == tab,
                    onClick = { selectedTabSearch = chips.indexOf(tab) },
                    label = { Text(tab) },
                    leadingIcon = if (chips[selectedTabSearch] == tab) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            val isCurrentStatusSearching = isSearching[selectedStatus] == true

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val items = searchResults[selectedStatus] ?: emptyList()

                items(items.size) { index ->
                    val userRate = items[index]
                    SearchAnimeTrackItem(userRate)

                    if (index >= items.size - 5 && shouldLoadMore.value) {
                        LaunchedEffect(selectedStatus) {
                            selectedStatus?.let {
                                tracksViewModel.searchAnimeTracks(searchQuery.query, status = it)
                            }
                        }
                    }
                }

                if (isCurrentStatusSearching) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}