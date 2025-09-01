package com.example.shikiflow.presentation.screen.browse

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.viewmodel.SearchViewModel

@Composable
fun BrowseScreen(
    searchViewModel: SearchViewModel = hiltViewModel(key = "browse_search"),
    browseNavOptions: BrowseNavOptions
) {
    val searchQuery by searchViewModel.screenState.collectAsState()
    val screenState by searchViewModel.screenState.collectAsState()

    BackHandler(enabled = screenState.isSearchActive) {
        searchViewModel.onSearchActiveChange(false)
        searchViewModel.clearSearchState()
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
                }, //modifier = Modifier.padding(top = 24.dp)
            )
        }
    ) { innerPadding ->
        Crossfade(screenState.isSearchActive) { isSearchActive ->
            if (isSearchActive) {
                BrowseSearchPage(
                    query = searchQuery.query,
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding(),
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)),
                    onMediaNavigate = { id, mediaType ->
                        browseNavOptions.navigateToDetails(id, mediaType)
                    }
                )
            } else {
                BrowseMainPage(
                    onNavigate = { id, mediaType ->
                        browseNavOptions.navigateToDetails(id, MediaType.ANIME)
                    },
                    onSideScreenNavigate = { sideScreen ->
                        browseNavOptions.navigateToSideScreen(sideScreen)
                    },
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding(),
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                    ),
                )
            }
        }
    }
}