package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.presentation.viewmodel.anime.BrowseViewModel

@Composable
fun BrowseMainPage(
    onNavigate: (String, MediaType) -> Unit,
    onSideScreenNavigate: (BrowseType) -> Unit,
    modifier: Modifier = Modifier,
    browseViewModel: BrowseViewModel = hiltViewModel()
) {
    val ongoingBrowseState = browseViewModel.paginatedBrowse(BrowseType.AnimeBrowseType.ONGOING)
        .collectAsLazyPagingItems()

    if(ongoingBrowseState.loadState.refresh is LoadState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }
    } else if(ongoingBrowseState.loadState.refresh is LoadState.Error) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { /*Retry Button?*/ }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(
                span = { GridItemSpan(3) }
            ) {
                NavigationSection(
                    onNavigateSideScreen = { sideScreen -> onSideScreenNavigate(sideScreen) }
                )
            }
            items(ongoingBrowseState.itemCount) { index ->
                ongoingBrowseState[index]?.let { browseItem ->
                    BrowseItem(
                        browseItem = browseItem,
                        onItemClick = onNavigate
                    )
                }
            }
            ongoingBrowseState.apply {
                if(loadState.append is LoadState.Loading) {
                    item(span = { GridItemSpan(3) }) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                }
            }
        }
    }
}