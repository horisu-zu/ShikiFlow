package com.example.shikiflow.presentation.screen.browse.side

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.browse.BrowseType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.mappers.BrowseTypeMapper.displayValue
import com.example.shikiflow.presentation.screen.browse.BrowseGridItem
import com.example.shikiflow.presentation.viewmodel.browse.side.BrowseSideViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainSideScreen(
    browseType: BrowseType,
    onMediaNavigate: (Int, MediaType) -> Unit,
    onBackNavigate: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        snapAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )
    var isAtTop by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = browseType.displayValue())
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { onBackNavigate() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    scrollBehavior = scrollBehavior
                )
                if(!isAtTop) { HorizontalDivider() }
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        MainSideScreenContent(
            browseType = browseType,
            onMediaNavigate = onMediaNavigate,
            onScrollStateChange = { isAtTop = it },
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding()
            )
        )
    }
}

@Composable
private fun MainSideScreenContent(
    browseType: BrowseType,
    onMediaNavigate: (Int, MediaType) -> Unit,
    onScrollStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    sideViewModel: BrowseSideViewModel = hiltViewModel()
) {
    val sideItems = sideViewModel.sideBrowseItems.collectAsLazyPagingItems()
    val lazyGridState = rememberLazyGridState()
    val isAtTop by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex == 0 &&
            lazyGridState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(browseType) {
        sideViewModel.setBrowseType(browseType)
    }

    LaunchedEffect(isAtTop) {
        onScrollStateChange(isAtTop)
    }

    when(sideItems.loadState.refresh) {
        is LoadState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }
        is LoadState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = stringResource(R.string.b_mss_error),
                    buttonLabel = stringResource(id = R.string.common_retry),
                    onButtonClick = { sideItems.refresh() }
                )
            }
        }
        else -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(120.dp),
                state = lazyGridState,
                modifier = modifier,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(sideItems.itemCount) { index ->
                    val browseItem = sideItems[index]!!
                    BrowseGridItem(
                        browseItem = browseItem,
                        onItemClick = { id, mediaType -> onMediaNavigate(id, mediaType) }
                    )
                }
                sideItems.apply {
                    if (loadState.append is LoadState.Loading) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                    }
                    if (loadState.append is LoadState.Error) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            ErrorItem(
                                message = stringResource(R.string.b_mss_error),
                                showFace = false,
                                buttonLabel = stringResource(R.string.common_retry),
                                onButtonClick = { sideItems.retry() }
                            )
                        }
                    }
                }
            }
        }
    }
}