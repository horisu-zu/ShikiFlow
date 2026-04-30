package com.example.shikiflow.presentation.screen.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    val currentTrackMode by mainScreenViewModel.currentTrackMode.collectAsStateWithLifecycle()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior(
        snapAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )

    val isScrolling by remember {
        derivedStateOf {
            scrollBehavior.scrollOffset <= scrollBehavior.scrollOffsetLimit
        }
    }

    val backgroundColor by animateColorAsState(
        targetValue = if(isScrolling) MaterialTheme.colorScheme.surfaceContainer
            else MaterialTheme.colorScheme.background
    )
    val itemColor by animateColorAsState(
        targetValue = if(isScrolling) MaterialTheme.colorScheme.background
            else MaterialTheme.colorScheme.surfaceContainer
    )

    currentTrackMode?.let { trackMode ->
        Scaffold(
            topBar = {
                TracksSearchBar(
                    currentTrackMode = trackMode,
                    scrollBehavior = scrollBehavior,
                    containerColor = backgroundColor,
                    itemColor = itemColor,
                    onModeChange = { trackMode -> mainScreenViewModel.setCurrentTrackMode(trackMode) },
                    mainNavOptions = mainNavOptions
                )
            },
            containerColor = backgroundColor,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { paddingValues ->
            MainPage(
                mediaType = trackMode,
                isAtTop = !isScrolling,
                onMediaClick = { mediaId, mediaType ->
                    val detailsNavRoute = when(mediaType) {
                        MediaType.ANIME -> DetailsNavRoute.AnimeDetails(mediaId)
                        MediaType.MANGA -> DetailsNavRoute.MangaDetails(mediaId)
                    }

                    mainNavOptions.navigateToDetails(detailsNavRoute)
                },
                modifier = Modifier
                    .padding(
                        top = maxOf(
                            paddingValues.calculateTopPadding(),
                            WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                        )
                    )
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}