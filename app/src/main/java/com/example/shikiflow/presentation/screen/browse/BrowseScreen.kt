package com.example.shikiflow.presentation.screen.browse

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.viewmodel.SearchViewModel

@Composable
fun BrowseScreen(
    searchViewModel: SearchViewModel = hiltViewModel(key = "browse_search"),
    browseNavOptions: BrowseNavOptions
) {
    val searchQuery by searchViewModel.searchQuery.collectAsStateWithLifecycle()
    val screenState by searchViewModel.screenState.collectAsStateWithLifecycle()
    var isAtTop by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            BrowseAppBar(
                title = stringResource(id = R.string.bottom_navigator_browse),
                searchQuery = screenState.query,
                isAtTop = isAtTop,
                onSearchQueryChange = searchViewModel::onQueryChange,
                isSearchActive = screenState.isSearchActive,
                onExitSearch = { searchViewModel.exitSearchState() },
                onSearchActiveChange = { isActive ->
                    searchViewModel.onSearchActiveChange(isActive)
                }, //modifier = Modifier.padding(top = 24.dp)
            )
        }
    ) { innerPadding ->
        Crossfade(screenState.isSearchActive) { isSearchActive ->
            if (isSearchActive) {
                BrowseSearchPage(
                    query = searchQuery,
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
                    onIsAtTopChange = { isAtTop = it },
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding(),
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                    ),
                )
            }
        }
    }
}