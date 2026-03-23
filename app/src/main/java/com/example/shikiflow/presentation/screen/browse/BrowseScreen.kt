package com.example.shikiflow.presentation.screen.browse

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.viewmodel.browse.search.BrowseSearchViewModel

@Composable
fun BrowseScreen(
    authType: AuthType,
    browseNavOptions: BrowseNavOptions,
    browseViewModel: BrowseSearchViewModel = hiltViewModel()
) {
    val screenState by browseViewModel.searchState.collectAsStateWithLifecycle()
    val browseOptions by browseViewModel.options.collectAsStateWithLifecycle()
    var isAtTop by remember { mutableStateOf(true) }

    val browseSearchData = browseViewModel.browseMediaItems.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            BrowseAppBar(
                title = stringResource(id = R.string.bottom_navigator_browse),
                searchQuery = screenState.query,
                isAtTop = isAtTop,
                onSearchQueryChange = browseViewModel::onQueryChange,
                isSearchActive = screenState.isSearchActive,
                onExitSearch = { browseViewModel.onSearchStateChange(false) },
                onSearchActiveChange = { isActive ->
                    browseViewModel.onSearchStateChange(isActive)
                },
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = screenState.isSearchActive,
            transitionSpec = {
                fadeIn() + scaleIn(
                    initialScale = 0.85f
                ) togetherWith fadeOut()
            }
        ) { isSearchActive ->
            when(isSearchActive) {
                true -> {
                    BrowseSearchPage(
                        browseSearchData = browseSearchData,
                        browseSearchEvent = browseViewModel,
                        authType = authType,
                        browseOptions = browseOptions,
                        modifier = Modifier.padding(
                            top = paddingValues.calculateTopPadding(),
                            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)),
                        onMediaNavigate = { id, mediaType ->
                            val detailsNavRoute = when(mediaType) {
                                MediaType.ANIME -> DetailsNavRoute.AnimeDetails(id)
                                MediaType.MANGA -> DetailsNavRoute.MangaDetails(id)
                            }

                            browseNavOptions.navigateToDetails(detailsNavRoute)
                        },
                        onIsAtTopChange = { isAtTop = it },
                    )
                }
                false -> {
                    BrowseMainPage(
                        authType = authType,
                        onNavigate = { id, mediaType ->
                            val detailsNavRoute = when(mediaType) {
                                MediaType.ANIME -> DetailsNavRoute.AnimeDetails(id)
                                MediaType.MANGA -> DetailsNavRoute.MangaDetails(id)
                            }

                            browseNavOptions.navigateToDetails(detailsNavRoute)
                        },
                        onSideScreenNavigate = { sideScreen ->
                            browseNavOptions.navigateToSideScreen(sideScreen)
                        },
                        onIsAtTopChange = { isAtTop = it },
                        modifier = Modifier.padding(
                            top = paddingValues.calculateTopPadding(),
                            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                        ),
                    )
                }
            }
        }
    }
}