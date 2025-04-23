package com.example.shikiflow.presentation.screen.browse

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.shikiflow.presentation.viewmodel.SearchViewModel

@Composable
fun BrowseScreen(
    searchViewModel: SearchViewModel = hiltViewModel(),
    browseNavController: NavController,
    rootNavController: NavController
) {
    val searchQuery by searchViewModel.screenState.collectAsState()
    val screenState by searchViewModel.screenState.collectAsState()

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
                    onNavigate = { id, mediaType ->
                        rootNavController.navigate("animeDetailsScreen/$id")
                    },
                    modifier = Modifier.padding(paddingValues).padding(horizontal = 12.dp),
                    browseNavController = browseNavController
                )
            }
        }
    }
}