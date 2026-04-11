package com.example.shikiflow.presentation.screen.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.MainNavOptions
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.viewmodel.MainScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainScreen(
    mainScreenViewModel: MainScreenViewModel = hiltViewModel(),
    mainNavOptions: MainNavOptions
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        snapAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    val currentTrackMode by mainScreenViewModel.currentTrackMode.collectAsStateWithLifecycle()
    val screenState by mainScreenViewModel.screenState.collectAsStateWithLifecycle()
    val searchQuery by mainScreenViewModel.searchQuery.collectAsStateWithLifecycle()

    currentTrackMode?.let { trackMode ->
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TracksSearchBar(
                    currentTrackMode = trackMode,
                    scrollBehavior = scrollBehavior,
                    query = screenState.query,
                    isSearchActive = screenState.isSearchActive,
                    onModeChange = { trackMode -> mainScreenViewModel.setCurrentTrackMode(trackMode) },
                    onQueryChange = mainScreenViewModel::onQueryChange,
                    onSearchToggle = mainScreenViewModel::onSearchActiveChange,
                    onExitSearch = mainScreenViewModel::exitSearchState
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                Crossfade(targetState = screenState.isSearchActive) { isSearchActive ->
                    if(isSearchActive) {
                        SearchPage(
                            searchQuery = searchQuery,
                            isAtTop = scrollBehavior.state.collapsedFraction < 1f,
                            mediaType = trackMode,
                            onMediaClick = { mediaType, id ->
                                val detailsNavRoute = when(mediaType) {
                                    MediaType.ANIME -> DetailsNavRoute.AnimeDetails(id)
                                    MediaType.MANGA -> DetailsNavRoute.MangaDetails(id)
                                }

                                mainNavOptions.navigateToDetails(detailsNavRoute)
                            }
                        )
                    } else {
                        MainPage(
                            mediaType = trackMode,
                            isAtTop = scrollBehavior.state.collapsedFraction < 1f,
                            isAppBarVisible = scrollBehavior.state.collapsedFraction == 0f,
                            onMediaClick = { mediaId, mediaType ->
                                val detailsNavRoute = when(mediaType) {
                                    MediaType.ANIME -> DetailsNavRoute.AnimeDetails(mediaId)
                                    MediaType.MANGA -> DetailsNavRoute.MangaDetails(mediaId)
                                }

                                mainNavOptions.navigateToDetails(detailsNavRoute)
                            }
                        )
                    }
                }
            }
        }
    }
}