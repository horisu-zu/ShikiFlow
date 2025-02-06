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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.graphql.AnimeBrowseQuery
import com.example.graphql.MangaBrowseQuery
import com.example.shikiflow.data.anime.BrowseState
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.presentation.viewmodel.anime.AnimeBrowseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseSideScreen(
    browseType: BrowseType,
    rootNavController: NavController,
    browseNavController: NavController,
    animeBrowseViewModel: AnimeBrowseViewModel = hiltViewModel()
) {
    val state = when(browseType) {
        is BrowseType.AnimeBrowseType -> animeBrowseViewModel.getAnimeState(browseType).collectAsState()
        is BrowseType.MangaBrowseType -> remember {
            mutableStateOf(BrowseState.MangaBrowseState())
        }
    }.value

    val items = when (state) {
        is BrowseState.AnimeBrowseState -> state.items
        is BrowseState.MangaBrowseState -> state.items
    }
    val isInitialized = remember {
        derivedStateOf {
            state.hasMorePages && items.isNotEmpty()
        }
    }

    LaunchedEffect(Unit) {
        if(!isInitialized.value) {
            when(browseType) {
                is BrowseType.AnimeBrowseType -> animeBrowseViewModel.browseAnime(type = browseType)
                is BrowseType.MangaBrowseType -> {
                    Log.d("BrowseSideScreen", "Manga browsing not yet implemented")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "duh",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { browseNavController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    ) {
        if(state.isLoading) {
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
                modifier = Modifier.padding(it).padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items.size) { index ->
                    when(val item = items[index]) {
                        is AnimeBrowseQuery.Anime -> {
                            BrowseItem(
                                anime = item,
                                onItemClick = { id ->
                                    rootNavController.navigate("animeDetailsScreen/$id")
                                }
                            )

                            if (index >= items.size - 3 && state.hasMorePages) {
                                animeBrowseViewModel.browseAnime(
                                    type = browseType,
                                    isLoadingMore = true
                                )
                            }
                        }
                        is MangaBrowseQuery.Manga -> {
                            // TODO: Render manga item when implemented
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
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}