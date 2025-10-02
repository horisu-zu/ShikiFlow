package com.example.shikiflow.presentation.screen.main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.MainScreenNavOptions
import com.example.shikiflow.presentation.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    currentUser: CurrentUserQuery.Data?,
    mainViewModel: MainViewModel = hiltViewModel(),
    navOptions: MainScreenNavOptions
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState,
        snapAnimationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    )

    val currentTrackMode by mainViewModel.currentTrackMode.collectAsStateWithLifecycle()
    val screenState by mainViewModel.screenState.collectAsStateWithLifecycle()
    val searchQuery by mainViewModel.searchQuery.collectAsStateWithLifecycle()

    currentTrackMode?.let { trackMode ->
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MainAppBar(
                    currentTrackMode = trackMode,
                    scrollBehavior = scrollBehavior,
                    user = currentUser,
                    query = screenState.query,
                    isSearchActive = screenState.isSearchActive,
                    onModeChange = { trackMode -> mainViewModel.setCurrentTrackMode(trackMode) },
                    onQueryChange = mainViewModel::onQueryChange,
                    onSearchToggle = mainViewModel::onSearchActiveChange,
                    onExitSearch = mainViewModel::exitSearchState
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                    )
            ) {
                Crossfade(targetState = screenState.isSearchActive) { isSearchActive ->
                    if(isSearchActive) {
                        SearchPage(
                            searchQuery = searchQuery,
                            isAtTop = scrollBehavior.state.collapsedFraction < 1f,
                            onAnimeClick = { animeId ->
                                navOptions.navigateToDetails(animeId, MediaType.ANIME)
                            }
                        )
                    } else {
                        currentTrackMode?.let { trackMode ->
                            MainPage(
                                mediaType = when(trackMode) {
                                    MainTrackMode.ANIME -> MediaType.ANIME
                                    MainTrackMode.MANGA -> MediaType.MANGA
                                },
                                isAtTop = scrollBehavior.state.collapsedFraction < 1f,
                                isAppBarVisible = scrollBehavior.state.collapsedFraction == 0f,
                                onAnimeClick = { animeId ->
                                    navOptions.navigateToDetails(animeId, MediaType.ANIME)
                                },
                                onMangaClick = { mangaId ->
                                    navOptions.navigateToDetails(mangaId, MediaType.MANGA)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}