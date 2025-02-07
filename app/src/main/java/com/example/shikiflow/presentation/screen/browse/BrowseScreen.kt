package com.example.shikiflow.presentation.screen.browse

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.presentation.viewmodel.SearchViewModel
import com.example.shikiflow.presentation.viewmodel.anime.AnimeBrowseViewModel

@Composable
fun BrowseScreen(
    browseViewModel: AnimeBrowseViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel(),
    browseNavController: NavController,
    rootNavController: NavController
) {
    val ongoingBrowseState by browseViewModel.getAnimeState(BrowseType.AnimeBrowseType.ONGOING)
        .collectAsState()
    val isInitialized = remember {
        derivedStateOf {
            ongoingBrowseState.hasMorePages && ongoingBrowseState.items.isEmpty()
        }
    }
    val searchQuery by searchViewModel.screenState.collectAsState()
    val screenState by searchViewModel.screenState.collectAsState()

    LaunchedEffect(Unit) {
        if (isInitialized.value) {
            browseViewModel.browseAnime(BrowseType.AnimeBrowseType.ONGOING)
        }
    }

    Scaffold(
        topBar = {
            BrowseAppBar(
                title = "Browse",
                searchQuery = searchQuery.query,
                onSearchQueryChange = searchViewModel::onQueryChange,
                isSearchActive = screenState.isSearchActive,
                onSearchActiveChange = { isActive ->
                    searchViewModel.onSearchActiveChange(isActive)
                    searchViewModel.clearSearchState()
                },
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    ) { paddingValues ->
        Crossfade(screenState.isSearchActive) { isSearchActive ->
            if (isSearchActive) {
                BrowseSearchPage(
                    query = searchQuery.query,
                    modifier = Modifier.padding(paddingValues).padding(horizontal = 12.dp),
                    rootNavController = rootNavController
                )
            } else {
                BrowseMainPage(
                    ongoingBrowseState = ongoingBrowseState,
                    onNavigate = { id ->
                        rootNavController.navigate("animeDetailsScreen/$id")
                    },
                    onLoadMore = {
                        browseViewModel.browseAnime(
                            type = BrowseType.AnimeBrowseType.ONGOING,
                            isLoadingMore = true
                        )
                    },
                    modifier = Modifier.padding(paddingValues).padding(horizontal = 12.dp),
                    browseNavController = browseNavController
                )
            }
        }
    }
}