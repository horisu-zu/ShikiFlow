package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.graphql.AnimeBrowseQuery
import com.example.graphql.MangaBrowseQuery
import com.example.shikiflow.data.anime.BrowseState
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.anime.toBrowseAnime
import com.example.shikiflow.data.anime.toBrowseManga
import com.example.shikiflow.data.tracks.MediaType

@Composable
fun MainSideScreen(
    state: BrowseState,
    rootNavController: NavController,
    onLoadMore: (MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = when (state) {
        is BrowseState.AnimeBrowseState -> state.items
        is BrowseState.MangaBrowseState -> state.items
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items.size) { index ->
            when(val item = items[index]) {
                is AnimeBrowseQuery.Anime -> {
                    BrowseItem(
                        browseItem = item.toBrowseAnime(),
                        onItemClick = { id ->
                            rootNavController.navigate("animeDetailsScreen/$id")
                        }
                    )
                }
                is MangaBrowseQuery.Manga -> {
                    BrowseItem(
                        browseItem = item.toBrowseManga(),
                        onItemClick = { id ->
                            rootNavController.navigate("mangaDetailsScreen/$id")
                        }
                    )
                }
            }
        }

        if (state.hasMorePages) {
            item(
                span = { GridItemSpan(3) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    onLoadMore(state.mediaType)
                    CircularProgressIndicator()
                }
            }
        }
    }
}