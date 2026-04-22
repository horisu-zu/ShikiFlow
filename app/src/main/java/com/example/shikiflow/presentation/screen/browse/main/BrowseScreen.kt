package com.example.shikiflow.presentation.screen.browse.main

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.browse.BrowseType
import com.example.shikiflow.domain.model.settings.AppUiMode
import com.example.shikiflow.domain.model.settings.BrowseUiMode
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.TextWithDivider
import com.example.shikiflow.presentation.screen.browse.main.anilist.AnilistBrowseMainPage
import com.example.shikiflow.presentation.screen.browse.BrowseGridItem
import com.example.shikiflow.presentation.screen.browse.BrowseListItem
import com.example.shikiflow.presentation.screen.browse.BrowseNavOptions
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.viewmodel.browse.main.BrowseViewModel
import com.example.shikiflow.presentation.viewmodel.browse.main.ShikiBrowseViewModel

@Composable
fun BrowseScreen(
    browseNavOptions: BrowseNavOptions,
    browseViewModel: BrowseViewModel = hiltViewModel()
) {
    val horizontalPadding = 12.dp
    var isAtTop by remember { mutableStateOf(false) }
    val authType by browseViewModel.authType.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            BrowseSearchBar(
                isAtMainTop = isAtTop,
                navOptions = browseNavOptions,
                horizontalPadding = horizontalPadding,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->
        authType?.let { authType ->
            when(authType) {
                AuthType.SHIKIMORI -> {
                    ShikimoriBrowseMainPage(
                        paddingValues = paddingValues,
                        horizontalPadding = horizontalPadding,
                        browseNavOptions = browseNavOptions,
                        onIsAtTopChange = { isAtTop = it }
                    )
                }
                AuthType.ANILIST -> {
                    AnilistBrowseMainPage(
                        paddingValues = paddingValues,
                        horizontalPadding = horizontalPadding,
                        browseNavOptions = browseNavOptions,
                        onIsAtTopChange = { isAtTop = it }
                    )
                }
            }
        }
    }
}

@Composable
fun ShikimoriBrowseMainPage(
    paddingValues: PaddingValues,
    horizontalPadding: Dp,
    browseNavOptions: BrowseNavOptions,
    onIsAtTopChange: (Boolean) -> Unit,
    browseViewModel: ShikiBrowseViewModel = hiltViewModel()
) {
    val browseUiSettings by browseViewModel.browseUiSettings.collectAsStateWithLifecycle()
    val ongoingBrowseState = browseViewModel.browseMainOngoingsState.collectAsLazyPagingItems()
    val showBottomSheet = remember { mutableStateOf(false) }

    var currentType by rememberSaveable { mutableStateOf(MediaType.ANIME) }
    val uiMode = when(browseUiSettings.browseUiMode) {
        BrowseUiMode.AUTO -> browseUiSettings.appUiMode
        BrowseUiMode.LIST -> AppUiMode.LIST
        BrowseUiMode.GRID -> AppUiMode.GRID
    }

    when (uiMode) {
        AppUiMode.LIST -> {
            BrowseListComponent(
                browseState = ongoingBrowseState,
                currentType = currentType,
                horizontalPadding = horizontalPadding,
                onSideScreenNavigate = { browseType ->
                    browseNavOptions.navigateToSideScreen(browseType)
                },
                onMediaTypeChange = { currentType = it },
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
                browseState = ongoingBrowseState,
                currentType = currentType,
                horizontalPadding = horizontalPadding,
                onSideScreenNavigate = { browseType ->
                    browseNavOptions.navigateToSideScreen(browseType)
                },
                onMediaTypeChange = { currentType = it },
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
            currentBrowseMode = browseUiSettings.browseUiMode,
            ongoingOrder = MediaSort.Shikimori.ongoingOptions,
            currentOngoingMode = browseUiSettings.browseOngoingOrder,
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

@Composable
fun BrowseListComponent(
    browseState: LazyPagingItems<BrowseMedia>,
    currentType: MediaType,
    horizontalPadding: Dp,
    onSideScreenNavigate: (BrowseType) -> Unit,
    onMediaTypeChange: (MediaType) -> Unit,
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

    LaunchedEffect(isAtTop) {
        onIsAtTopChange(isAtTop)
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
                currentType = currentType,
                onMediaTypeChange = onMediaTypeChange,
                onNavigateSideScreen = { sideScreen -> onSideScreenNavigate(sideScreen) }
            )
        }

        item { OngoingTitleComponent(onSettingClick = onSettingClick) }

        if(browseState.loadState.refresh is LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
        } else if(browseState.loadState.refresh is LoadState.Error) {
            item {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { browseState.refresh() }
                    )
                }
            }
        } else {
            items(
                count = browseState.itemCount,
                key = browseState.itemKey { it.id }
            ) { index ->
                browseState[index]?.let { browseItem ->
                    BrowseListItem(
                        browseItem = browseItem as BrowseMedia.Anime,
                        onItemClick = onNavigate
                    )
                }
            }
            if(browseState.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }
            if(browseState.loadState.append is LoadState.Error) {
                item {
                    ErrorItem(
                        message = stringResource(R.string.b_mss_error),
                        showFace = false,
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { browseState.retry() }
                    )
                }
            }
        }
    }
}

@Composable
fun BrowseGridComponent(
    browseState: LazyPagingItems<BrowseMedia>,
    currentType: MediaType,
    horizontalPadding: Dp,
    onSideScreenNavigate: (BrowseType) -> Unit,
    onMediaTypeChange: (MediaType) -> Unit,
    onNavigate: (Int, MediaType) -> Unit,
    onSettingClick: () -> Unit,
    onIsAtTopChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyGridState = rememberLazyGridState()
    val windowInfo = LocalWindowInfo.current

    val isAtTop by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex == 0 &&
            lazyGridState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(isAtTop) {
        onIsAtTopChange(isAtTop)
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
                currentType = currentType,
                onMediaTypeChange = onMediaTypeChange,
                onNavigateSideScreen = { sideScreen -> onSideScreenNavigate(sideScreen) }
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            OngoingTitleComponent(
                onSettingClick = onSettingClick
            )
        }

        when (browseState.loadState.refresh) {
            is LoadState.Loading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(windowInfo.containerDpSize.height * 0.65f),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }
            is LoadState.Error -> {
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
                            onButtonClick = { browseState.refresh() }
                        )
                    }
                }
            }
            else -> {
                items(
                    count = browseState.itemCount,
                    key = browseState.itemKey { it.id }
                ) { index ->
                    browseState[index]?.let { browseItem ->
                        BrowseGridItem(
                            browseItem = browseItem,
                            onItemClick = onNavigate
                        )
                    }
                }
                browseState.apply {
                    if (loadState.append is LoadState.Loading) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                    }
                    if (browseState.loadState.append is LoadState.Error) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            ErrorItem(
                                message = stringResource(R.string.b_mss_error),
                                showFace = false,
                                buttonLabel = stringResource(R.string.common_retry),
                                onButtonClick = { browseState.retry() }
                            )
                        }
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

        IconButton(onClick = { onSettingClick() }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null
            )
        }
    }
}