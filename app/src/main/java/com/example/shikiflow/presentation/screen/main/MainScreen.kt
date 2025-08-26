package com.example.shikiflow.presentation.screen.main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.domain.model.mapper.UserRateStatusConstants
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.MainScreenNavOptions
import com.example.shikiflow.presentation.screen.main.mangatrack.MainMangaPage
import com.example.shikiflow.presentation.viewmodel.SearchViewModel
import com.example.shikiflow.utils.AppSettingsManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    appSettingsManager: AppSettingsManager,
    currentUser: CurrentUserQuery.Data?,
    searchViewModel: SearchViewModel = hiltViewModel(key = "main_search"),
    navOptions: MainScreenNavOptions
) {
    val scope = rememberCoroutineScope()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState,
        snapAnimationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    )
    val currentTrackMode by appSettingsManager.trackModeFlow.collectAsState(initial = MainTrackMode.ANIME)
    val screenState by searchViewModel.screenState.collectAsState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainAppBar(
                currentTrackMode = currentTrackMode,
                scrollBehavior = scrollBehavior,
                user = currentUser,
                query = screenState.query,
                isSearchActive = screenState.isSearchActive,
                onModeChange = { trackMode ->
                    scope.launch {
                        appSettingsManager.saveTrackMode(trackMode)
                    }
                },
                onQueryChange = searchViewModel::onQueryChange,
                onSearchToggle = searchViewModel::onSearchActiveChange
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
            when(currentTrackMode) {
                MainTrackMode.ANIME -> {
                    val pagerState = rememberPagerState { UserRateStatusConstants.getStatusChips(MediaType.ANIME).size }
                    Crossfade(targetState = screenState.isSearchActive) { isSearchActive ->
                        if (isSearchActive) {
                            SearchPage(onAnimeClick = { animeId ->
                                navOptions.navigateToDetails(animeId, MediaType.ANIME)
                            })
                        } else {
                            MainPage(
                                pagerState = pagerState,
                                onAnimeClick = { animeId ->
                                    navOptions.navigateToDetails(animeId, MediaType.ANIME)
                                }
                            )
                        }
                    }
                }
                MainTrackMode.MANGA -> {
                    MainMangaPage(
                        onMangaClick = { mangaId ->
                            navOptions.navigateToDetails(mangaId, MediaType.MANGA)
                        }
                    )
                }
            }
        }
    }
}