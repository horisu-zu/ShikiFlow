package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.shikiflow.data.anime.Browse
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.presentation.screen.MainNavRoute

@Composable
fun MainSideScreen(
    browseData: LazyPagingItems<Browse>,
    rootNavController: NavController,
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
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Error Loading Browse Data"
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        browseData.refresh()
                    }
                ) {
                    Text(text = "Retry")
                }
            }
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
                BrowseItem(
                    browseItem = browseItem,
                    onItemClick = { id, mediaType ->
                        if(mediaType == MediaType.ANIME) {
                            rootNavController.navigate(MainNavRoute.AnimeDetails(id))
                        } else {
                            rootNavController.navigate(MainNavRoute.MangaDetails(id))
                        }
                    }
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