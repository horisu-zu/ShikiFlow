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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem

@Composable
fun MainSideScreen(
    browseData: LazyPagingItems<Browse>,
    onMediaNavigate: (String, MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    if(browseData.loadState.refresh is LoadState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }
    } else if(browseData.loadState.refresh is LoadState.Error) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ErrorItem(
                message = stringResource(R.string.b_mss_error),
                buttonLabel = stringResource(id = R.string.common_retry),
                onButtonClick = { browseData.refresh() }
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(browseData.itemCount) { index ->
                val browseItem = browseData[index]!!
                BrowseGridItem(
                    browseItem = browseItem,
                    onItemClick = { id, mediaType -> onMediaNavigate(id, mediaType) }
                )
            }
            browseData.apply {
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