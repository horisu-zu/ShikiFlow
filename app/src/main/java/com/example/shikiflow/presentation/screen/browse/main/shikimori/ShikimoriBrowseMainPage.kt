package com.example.shikiflow.presentation.screen.browse.main.shikimori

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.browse.BrowseType
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.settings.AppUiMode
import com.example.shikiflow.domain.model.settings.BrowseUiMode
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.TextWithDivider
import com.example.shikiflow.presentation.screen.browse.BrowseGridItem
import com.example.shikiflow.presentation.screen.browse.BrowseGridItemPlaceholder
import com.example.shikiflow.presentation.screen.browse.BrowseAnimeListItem
import com.example.shikiflow.presentation.screen.browse.BrowseAnimeListItemPlaceholder
import com.example.shikiflow.presentation.screen.browse.BrowseMangaListItem
import com.example.shikiflow.presentation.screen.browse.BrowseNavOptions
import com.example.shikiflow.presentation.screen.browse.main.BrowseMainBottomSheet
import com.example.shikiflow.presentation.screen.browse.main.NavigationSection
import com.example.shikiflow.presentation.screen.main.LocalTitleTypeController
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.viewmodel.browse.main.shikimori.ShikiBrowseEvent
import com.example.shikiflow.presentation.viewmodel.browse.main.shikimori.ShikiBrowseMediaUiState
import com.example.shikiflow.presentation.viewmodel.browse.main.shikimori.ShikiBrowseUiState
import com.example.shikiflow.presentation.viewmodel.browse.main.shikimori.ShikiBrowseViewModel
import com.example.shikiflow.utils.LazyListUtils.onBottomReached

@Composable
fun ShikimoriBrowseMainPage(
    paddingValues: PaddingValues,
    horizontalPadding: Dp,
    browseNavOptions: BrowseNavOptions,
    onIsAtTopChange: (Boolean) -> Unit,
    browseViewModel: ShikiBrowseViewModel = hiltViewModel()
) {
    val preferredTitleType = LocalTitleTypeController.current
    val browseUiSettings by browseViewModel.browseUiSettings.collectAsStateWithLifecycle()
    val uiState by browseViewModel.uiState.collectAsStateWithLifecycle()
    val showBottomSheet = remember { mutableStateOf(false) }

    val uiMode = browseUiSettings.browseUiMode?.let { browseUiMode ->
        when(browseUiMode) {
            BrowseUiMode.AUTO -> browseUiSettings.appUiMode
            BrowseUiMode.LIST -> AppUiMode.LIST
            BrowseUiMode.GRID -> AppUiMode.GRID
        }
    }

    uiMode?.let {
        when (uiMode) {
            AppUiMode.LIST -> {
                BrowseListComponent(
                    uiState = uiState,
                    browseEvent = browseViewModel,
                    preferredTitleType = preferredTitleType,
                    horizontalPadding = horizontalPadding,
                    onSideScreenNavigate = { browseType ->
                        browseNavOptions.navigateToSideScreen(browseType)
                    },
                    onNavigate = { id, mediaType ->
                        val detailsNavRoute = when(mediaType) {
                            MediaType.ANIME -> DetailsNavRoute.AnimeDetails(id)
                            MediaType.MANGA -> DetailsNavRoute.MangaDetails(id)
                        }

                        browseNavOptions.navigateToDetails(detailsNavRoute)
                    },
                    onSettingClick = { showBottomSheet.value = true },
                    onIsAtTopChange = onIsAtTopChange,
                    modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
                )
            }
            AppUiMode.GRID -> {
                BrowseGridComponent(
                    uiState = uiState,
                    browseEvent = browseViewModel,
                    preferredTitleType = preferredTitleType,
                    horizontalPadding = horizontalPadding,
                    onSideScreenNavigate = { browseType ->
                        browseNavOptions.navigateToSideScreen(browseType)
                    },
                    onNavigate = { id, mediaType ->
                        val detailsNavRoute = when(mediaType) {
                            MediaType.ANIME -> DetailsNavRoute.AnimeDetails(id)
                            MediaType.MANGA -> DetailsNavRoute.MangaDetails(id)
                        }

                        browseNavOptions.navigateToDetails(detailsNavRoute)
                    },
                    onSettingClick = { showBottomSheet.value = true },
                    onIsAtTopChange = onIsAtTopChange,
                    modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
                )
            }
        }

        if(showBottomSheet.value) {
            BrowseMainBottomSheet(
                currentBrowseMode = browseUiSettings.browseUiMode ?: BrowseUiMode.AUTO,
                ongoingOrder = MediaSort.Shikimori.ongoingOptions,
                currentOngoingMode = browseUiSettings.browseOngoingOrder ?: MediaSort.Common.POPULARITY,
                onDismiss = { showBottomSheet.value = false },
                onModeSelect = { newMode ->
                    browseViewModel.setBrowseUiMode(newMode)
                },
                onSortSelect = { newOrder ->
                    browseViewModel.setBrowseOngoingOrder(newOrder)
                }
            )
        }
    }
}

@Composable
fun BrowseListComponent(
    uiState: ShikiBrowseUiState,
    browseEvent: ShikiBrowseEvent,
    preferredTitleType: PreferredTitleType,
    horizontalPadding: Dp,
    onSideScreenNavigate: (BrowseType) -> Unit,
    onNavigate: (Int, MediaType) -> Unit,
    onSettingClick: () -> Unit,
    onIsAtTopChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
            lazyListState.firstVisibleItemScrollOffset == 0
        }
    }
    val sectionState = uiState.mainSections[uiState.mediaType] ?: ShikiBrowseMediaUiState()

    LaunchedEffect(isAtTop) {
        onIsAtTopChange(isAtTop)
    }

    if (!sectionState.isLoading) {
        lazyListState.onBottomReached(
            buffer = 6,
            onLoadMore = { browseEvent.loadMore() }
        )
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        contentPadding = PaddingValues(
            start = horizontalPadding,
            end = horizontalPadding,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
            top = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            NavigationSection(
                currentType = uiState.mediaType,
                onMediaTypeChange = { type -> browseEvent.setMediaType(type) },
                onNavigateSideScreen = { sideScreen -> onSideScreenNavigate(sideScreen) }
            )
        }

        item { OngoingTitleComponent(onSettingClick = onSettingClick) }

        if (sectionState.errorMessage != null && sectionState.entries.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { browseEvent.refresh() }
                    )
                }
            }
        } else {
            items(count = sectionState.entries.size) { index ->
                when (val browseItem = sectionState.entries[index]) {
                    is BrowseMedia.Anime -> {
                        BrowseAnimeListItem(
                            browseItem = browseItem,
                            titleType = preferredTitleType,
                            onItemClick = onNavigate
                        )
                    }
                    is BrowseMedia.Manga -> {
                        BrowseMangaListItem(
                            browseItem = browseItem,
                            titleType = preferredTitleType,
                            onItemClick = onNavigate
                        )
                    }
                }
            }

            if (sectionState.isLoading) {
                items(count = 9) { index ->
                    BrowseAnimeListItemPlaceholder(itemIndex = index % 3 + 1)
                }
            } else if (sectionState.errorMessage != null) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = stringResource(R.string.common_error),
                            buttonLabel = stringResource(R.string.common_retry),
                            onButtonClick = { browseEvent.retry() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BrowseGridComponent(
    uiState: ShikiBrowseUiState,
    browseEvent: ShikiBrowseEvent,
    preferredTitleType: PreferredTitleType,
    horizontalPadding: Dp,
    onSideScreenNavigate: (BrowseType) -> Unit,
    onNavigate: (Int, MediaType) -> Unit,
    onSettingClick: () -> Unit,
    onIsAtTopChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyGridState = rememberLazyGridState()
    val windowInfo = LocalWindowInfo.current
    val sectionState = uiState.mainSections[uiState.mediaType] ?: ShikiBrowseMediaUiState()

    val isAtTop by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex == 0 &&
            lazyGridState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(isAtTop) {
        onIsAtTopChange(isAtTop)
    }

    if (!sectionState.isLoading) {
        lazyGridState.onBottomReached(
            buffer = 6,
            onLoadMore = { browseEvent.loadMore() }
        )
    }

    LazyVerticalGrid(
        state = lazyGridState,
        columns = GridCells.Adaptive(120.dp),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            start = horizontalPadding,
            end = horizontalPadding,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
            top = 8.dp
        )
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            NavigationSection(
                currentType = uiState.mediaType,
                onMediaTypeChange = { type -> browseEvent.setMediaType(type) },
                onNavigateSideScreen = { sideScreen -> onSideScreenNavigate(sideScreen) }
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            OngoingTitleComponent(
                onSettingClick = onSettingClick
            )
        }

        if (sectionState.errorMessage != null && sectionState.entries.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(windowInfo.containerDpSize.height * 0.65f),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { browseEvent.refresh() }
                    )
                }
            }
        } else {
            items(count = sectionState.entries.size) { index ->
                BrowseGridItem(
                    browseItem = sectionState.entries[index],
                    titleType = preferredTitleType,
                    onItemClick = onNavigate
                )
            }

            if (sectionState.isLoading) {
                items(count = 9) {
                    BrowseGridItemPlaceholder()
                }
            } else if (sectionState.errorMessage != null) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = stringResource(R.string.common_error),
                            buttonLabel = stringResource(R.string.common_retry),
                            onButtonClick = { browseEvent.retry() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OngoingTitleComponent(
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextWithDivider(
            text = stringResource(R.string.browse_ongoings),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )

        IconButton(
            onClick = { onSettingClick() },
            shape = RoundedCornerShape(percent = 24)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sort),
                contentDescription = null
            )
        }
    }
}