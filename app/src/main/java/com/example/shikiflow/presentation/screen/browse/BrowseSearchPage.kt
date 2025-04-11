package com.example.shikiflow.presentation.screen.browse

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.graphql.AnimeBrowseQuery
import com.example.graphql.MangaBrowseQuery
import com.example.shikiflow.data.anime.BrowseState
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.anime.toBrowseAnime
import com.example.shikiflow.data.anime.toBrowseManga
import com.example.shikiflow.data.mapper.BrowseOptions
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.presentation.viewmodel.anime.AnimeBrowseViewModel
import com.example.shikiflow.presentation.viewmodel.manga.MangaBrowseViewModel
import kotlinx.coroutines.delay

@Composable
fun BrowseSearchPage(
    query: String,
    modifier: Modifier = Modifier,
    animeBrowseViewModel: AnimeBrowseViewModel = hiltViewModel(),
    mangaBrowseViewModel: MangaBrowseViewModel = hiltViewModel(),
    rootNavController: NavController
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var searchOptions by remember {
        mutableStateOf(BrowseOptions(mediaType = MediaType.ANIME))
    }
    val animeState = animeBrowseViewModel.getAnimeState(BrowseType.AnimeBrowseType.SEARCH).collectAsState()
    val mangaState = mangaBrowseViewModel.getMangaState(BrowseType.MangaBrowseType.SEARCH).collectAsState()

    val state = when (searchOptions.mediaType) {
        MediaType.ANIME -> animeState.value
        MediaType.MANGA -> mangaState.value
    }
    val items = when (state) {
        is BrowseState.AnimeBrowseState -> state.items
        is BrowseState.MangaBrowseState -> state.items
    }

    LaunchedEffect(query, searchOptions) {
        if (query.isEmpty()) return@LaunchedEffect
        delay(500)
        Log.d("Search Page", "Query: $query")
        when (searchOptions.mediaType) {
            MediaType.ANIME -> animeBrowseViewModel.browseAnime(
                type = BrowseType.AnimeBrowseType.SEARCH,
                options = searchOptions,
                name = query
            )

            MediaType.MANGA -> mangaBrowseViewModel.browseManga(
                type = BrowseType.MangaBrowseType.SEARCH,
                options = searchOptions,
                name = query
            )
        }
    }

    Box {
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items.size) { index ->
                    when (val item = items[index]) {
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
                if (state.hasMorePages && query.isNotEmpty()) {
                    item(
                        span = { GridItemSpan(3) }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                            when (searchOptions.mediaType) {
                                MediaType.ANIME -> animeBrowseViewModel.browseAnime(
                                    type = BrowseType.AnimeBrowseType.SEARCH,
                                    options = searchOptions,
                                    name = query,
                                    isLoadingMore = true
                                )

                                MediaType.MANGA -> mangaBrowseViewModel.browseManga(
                                    type = BrowseType.MangaBrowseType.SEARCH,
                                    options = searchOptions,
                                    name = query,
                                    isLoadingMore = true
                                )
                            }
                        }
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
            searchOptions = searchOptions,
            onOptionsChanged = { newOptions ->
                searchOptions = newOptions
            },
            onDismiss = { showBottomSheet = false }
        )
    }
}