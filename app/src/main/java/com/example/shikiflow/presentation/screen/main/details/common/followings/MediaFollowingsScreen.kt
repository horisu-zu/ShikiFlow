package com.example.shikiflow.presentation.screen.main.details.common.followings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.sort.UserRateType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.SortBottomSheet
import com.example.shikiflow.presentation.common.SortConfig
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.followings.MediaFollowingsViewModel

@Composable
fun MediaFollowingsScreen(
    mediaId: Int,
    totalCount: Int?,
    navOptions: MediaNavOptions,
    mediaFollowingsViewModel: MediaFollowingsViewModel = hiltViewModel()
) {
    val params by mediaFollowingsViewModel.params.collectAsStateWithLifecycle()
    val mediaFollowingItems = mediaFollowingsViewModel.mediaFollowings.collectAsLazyPagingItems()

    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(mediaId) {
        mediaFollowingsViewModel.setMediaId(mediaId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_sort),
                    contentDescription = "Show Sort Bottom Sheet"
                )
            }
        }
    ) { paddingValues ->
        when(mediaFollowingItems.loadState.refresh) {
            is LoadState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = (mediaFollowingItems.loadState.refresh as LoadState.Error)
                            .error.message ?: stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { mediaFollowingItems.refresh() }
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(320.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        end = 12.dp,
                        top = paddingValues.calculateTopPadding(),
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
                ) {
                    if(mediaFollowingItems.loadState.refresh is LoadState.Loading) {
                        items(6) {
                            MediaFollowingItemPlaceholder(
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        items(mediaFollowingItems.itemCount) { index ->
                            mediaFollowingItems[index]?.let { mediaFollowing ->
                                MediaFollowingItem(
                                    mediaFollowing = mediaFollowing,
                                    totalCount = totalCount,
                                    onUserClick = { user ->
                                        navOptions.navigateToUserProfile(user)
                                    }
                                )
                            }
                        }
                        mediaFollowingItems.apply {
                            when {
                                loadState.append is LoadState.Error -> {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            ErrorItem(
                                                message = stringResource(R.string.common_error),
                                                buttonLabel = stringResource(R.string.common_retry),
                                                onButtonClick = { mediaFollowingItems.retry() }
                                            )
                                        }
                                    }
                                }

                                loadState.append is LoadState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) { CircularProgressIndicator() }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if(showBottomSheet) {
            SortBottomSheet(
                config = SortConfig(
                    options = UserRateType.entries,
                    selected = params.sort,
                    onSortChange = { userRateSort ->
                        mediaFollowingsViewModel.setSort(userRateSort)
                    }
                ),
                onDismiss = { showBottomSheet = false }
            )
        }
    }
}