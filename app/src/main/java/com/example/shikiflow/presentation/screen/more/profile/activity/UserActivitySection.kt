package com.example.shikiflow.presentation.screen.more.profile.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.activity.ListActivity
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.PullToRefreshCustomBox
import com.example.shikiflow.presentation.screen.main.LocalTitleTypeController
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavOptions
import com.example.shikiflow.presentation.viewmodel.user.activity.UserActivityViewModel
import com.example.shikiflow.utils.LazyListUtils.onBottomReached

@Composable
fun UserActivitySection(
    userId: Int,
    isRefreshEnabled: Boolean,
    horizontalPadding: Dp,
    onRefresh: () -> Unit,
    navOptions: ProfileNavOptions,
    modifier: Modifier = Modifier,
    userActivityViewModel: UserActivityViewModel = hiltViewModel()
) {
    val preferredTitleType = LocalTitleTypeController.current
    val lazyListState = rememberLazyGridState()
    val uiState by userActivityViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        userActivityViewModel.setId(userId)
    }

    if (!uiState.isLoading) {
        lazyListState.onBottomReached(
            buffer = 5,
            onLoadMore = { userActivityViewModel.onLoadMore() }
        )
    }

    PullToRefreshCustomBox(
        isRefreshing = uiState.isRefreshing,
        modifier = modifier.fillMaxSize(),
        enabled = isRefreshEnabled,
        onRefresh = {
            onRefresh()
            userActivityViewModel.refresh()
        }
    ) {
        if (uiState.errorMessage != null && uiState.items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = stringResource(R.string.common_error),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { userActivityViewModel.refresh() }
                )
            }
        } else {
            LazyVerticalGrid(
                state = lazyListState,
                columns = GridCells.Adaptive(300.dp),
                userScrollEnabled = !uiState.isRefreshing,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(
                    horizontal = horizontalPadding,
                    vertical = 8.dp
                )
            ) {
                if (uiState.isLoading && uiState.items.isEmpty()) {
                    items(12) { index ->
                        ListActivityItemPlaceholder(
                            itemIndex = index
                        )
                    }
                } else if (uiState.items.isNotEmpty()) {
                    items(
                        items = uiState.items,
                        span = { item ->
                            GridItemSpan(
                                currentLineSpan = when(item) {
                                    is ListActivity -> 1
                                    else -> maxLineSpan
                                }
                            )
                        }
                    ) { activityItem ->
                        ActivityItem(
                            userActivity = activityItem,
                            titleType = preferredTitleType,
                            onListActivityClick = { mediaType, id ->
                                val detailsNavRoute = when(mediaType) {
                                    MediaType.ANIME -> DetailsNavRoute.AnimeDetails(id)
                                    MediaType.MANGA -> DetailsNavRoute.MangaDetails(id)
                                }

                                navOptions.navigateToDetails(detailsNavRoute)
                            },
                            onEntityClick = { entityType, id ->
                                navOptions.navigateByEntity(entityType, id)
                            },
                            onLikeToggle = {
                                userActivityViewModel.toggleLike(activityItem.id)
                            },
                            onRepliesClick = {
                                navOptions.navigateToActivityReplies(
                                    activityId = activityItem.id,
                                    activityType = activityItem.type
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (uiState.errorMessage != null) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ErrorItem(
                                    message = stringResource(R.string.common_error),
                                    buttonLabel = stringResource(R.string.common_retry),
                                    onButtonClick = { userActivityViewModel.retry() }
                                )
                            }
                        }
                    } else if (uiState.isLoading) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
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