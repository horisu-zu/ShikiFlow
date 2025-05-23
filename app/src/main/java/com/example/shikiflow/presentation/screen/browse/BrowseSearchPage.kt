package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.mapper.BrowseOptions
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.presentation.viewmodel.anime.BrowseViewModel

@Composable
fun BrowseSearchPage(
    query: String,
    onMediaNavigate: (String, MediaType) -> Unit,
    modifier: Modifier = Modifier,
    browseViewModel: BrowseViewModel = hiltViewModel()
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentType by rememberSaveable { mutableStateOf<BrowseType>(BrowseType.AnimeBrowseType.SEARCH) }
    var searchOptions by remember { mutableStateOf(BrowseOptions()) }

    val browseSearchData = browseViewModel.paginatedBrowse(
        type = currentType,
        options = searchOptions,
        name = query
    ).collectAsLazyPagingItems()

    Box {
        if(browseSearchData.loadState.refresh is LoadState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(browseSearchData.itemCount) { index ->
                    browseSearchData[index]?.let { browseItem ->
                        BrowseItem(
                            browseItem = browseItem,
                            onItemClick = { id, mediaType -> onMediaNavigate(id, mediaType) }
                        )
                    }
                }
                if(browseSearchData.loadState.append is LoadState.Loading) {
                    item(span = { GridItemSpan(3) }) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                }
            }
        }
        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.surface,
            onClick = { showBottomSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.List, "Show filters")
        }
    }

    if (showBottomSheet) {
        SearchBottomSheet(
            currentType = currentType,
            searchOptions = searchOptions,
            onOptionsChanged = { newOptions -> searchOptions = newOptions },
            onTypeChanged = { newType -> currentType = newType },
            onDismiss = { showBottomSheet = false }
        )
    }
}