package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.viewmodel.anime.BrowseViewModel
import com.example.shikiflow.utils.AppUiMode
import com.example.shikiflow.utils.BrowseUiMode

@Composable
fun BrowseMainPage(
    onNavigate: (String, MediaType) -> Unit,
    onSideScreenNavigate: (BrowseType) -> Unit,
    modifier: Modifier = Modifier,
    onIsAtTopChange: (Boolean) -> Unit,
    browseViewModel: BrowseViewModel = hiltViewModel()
) {
    val browseUiSettings by browseViewModel.browseUiSettings.collectAsStateWithLifecycle()
    val ongoingBrowseState = browseViewModel.browseMainOngoingsState.collectAsLazyPagingItems()
    val showBottomSheet = remember { mutableStateOf(false) }

    val uiMode = when(browseUiSettings.browseUiMode) {
        BrowseUiMode.AUTO -> browseUiSettings.appUiMode
        BrowseUiMode.LIST -> AppUiMode.LIST
        BrowseUiMode.GRID -> AppUiMode.GRID
    }

    when (uiMode) {
        AppUiMode.LIST -> {
            BrowseListComponent(
                browseState = ongoingBrowseState,
                onSideScreenNavigate = onSideScreenNavigate,
                onNavigate = onNavigate,
                onSettingClick = { showBottomSheet.value = true },
                onIsAtTopChange = onIsAtTopChange,
                modifier = modifier
            )
        }
        AppUiMode.GRID -> {
            BrowseGridComponent(
                browseState = ongoingBrowseState,
                onSideScreenNavigate = onSideScreenNavigate,
                onNavigate = onNavigate,
                onSettingClick = { showBottomSheet.value = true },
                onIsAtTopChange = onIsAtTopChange,
                modifier = modifier
            )
        }
    }
    if(showBottomSheet.value) {
        BrowseMainBottomSheet(
            currentBrowseMode = browseUiSettings.browseUiMode,
            currentOngoingMode = browseUiSettings.browseOngoingOrder,
            onDismiss = { showBottomSheet.value = false },
            onModeSelect = { newMode ->
                browseViewModel.setBrowseUiMode(newMode)
            },
            onOrderSelect = { newOrder ->
                browseViewModel.setBrowseOngoingOrder(newOrder)
            }
        )
    }
}

@Composable
private fun BrowseListComponent(
    browseState: LazyPagingItems<Browse>,
    onSideScreenNavigate: (BrowseType) -> Unit,
    onNavigate: (String, MediaType) -> Unit,
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
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            NavigationSection(
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
                        browseItem = browseItem as Browse.Anime,
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
private fun BrowseGridComponent(
    browseState: LazyPagingItems<Browse>,
    onSideScreenNavigate: (BrowseType) -> Unit,
    onNavigate: (String, MediaType) -> Unit,
    onSettingClick: () -> Unit,
    onIsAtTopChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyGridState = rememberLazyGridState()
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
        columns = GridCells.Fixed(3),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 8.dp)
    ) {
        item(span = { GridItemSpan(3) }) {
            NavigationSection(
                onNavigateSideScreen = { sideScreen -> onSideScreenNavigate(sideScreen) }
            )
        }

        item(span = { GridItemSpan(3) }) {
            OngoingTitleComponent(onSettingClick = onSettingClick)
        }

        if(browseState.loadState.refresh is LoadState.Loading) {
            item(span = { GridItemSpan(3) }) {
                Box(
                    modifier = Modifier.fillMaxSize().heightIn(240.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
        } else if(browseState.loadState.refresh is LoadState.Error) {
            item(span = { GridItemSpan(3) }) {
                Box(
                    modifier = Modifier.fillMaxSize().heightIn(240.dp),
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
                    BrowseGridItem(
                        browseItem = browseItem,
                        onItemClick = onNavigate
                    )
                }
            }
            browseState.apply {
                if(loadState.append is LoadState.Loading) {
                    item(span = { GridItemSpan(3) }) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                }
                if(browseState.loadState.append is LoadState.Error) {
                    item(span = { GridItemSpan(3) }) {
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
        Text(
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