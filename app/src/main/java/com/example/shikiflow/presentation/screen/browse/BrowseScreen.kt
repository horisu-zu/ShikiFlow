package com.example.shikiflow.presentation.screen.browse

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.shikiflow.presentation.screen.MainNavRoute
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
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)).padding(horizontal = 12.dp),
                    rootNavController = rootNavController
                )
            } else {
                BrowseMainPage(
                    onNavigate = { id, mediaType ->
                        rootNavController.navigate(MainNavRoute.AnimeDetails(id))
                    },
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding(),
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)).padding(horizontal = 12.dp),
                    browseNavController = browseNavController
                )
            }
        }
    }
}