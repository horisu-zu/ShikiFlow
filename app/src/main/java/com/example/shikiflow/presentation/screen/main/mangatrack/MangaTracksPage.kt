package com.example.shikiflow.presentation.screen.main.mangatrack

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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrack
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrack.Companion.toBrowse
import com.example.shikiflow.presentation.screen.browse.BrowseItem

@Composable
fun MangaTracksPage(
    trackItems: LazyPagingItems<MangaTrack>?,
    onMangaClick: (String) -> Unit
) {
    trackItems?.let { items ->
        if(trackItems.loadState.refresh is LoadState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if(trackItems.loadState.refresh is LoadState.Error) {
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
                        onClick = { trackItems.refresh() }
                    ) {
                        Text(text = "Retry")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                items(trackItems.itemCount) { index ->
                    trackItems[index]?.toBrowse()?.let { mangaItem ->
                        BrowseItem(
                            browseItem = mangaItem,
                            onItemClick = { id, mediaType ->
                                onMangaClick(id)
                            }
                        )
                    }
                }
                trackItems.apply {
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
}