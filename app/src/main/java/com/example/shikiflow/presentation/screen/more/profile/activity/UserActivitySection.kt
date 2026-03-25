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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.user.ListActivity
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.PullToRefreshCustomBox
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.more.history.ActivityItem
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavOptions
import com.example.shikiflow.presentation.viewmodel.user.activity.UserActivityViewModel
import com.example.shikiflow.utils.WebIntent

@Composable
fun UserActivitySection(
    userId: Int,
    isRefreshEnabled: Boolean,
    horizontalPadding: Dp,
    navOptions: ProfileNavOptions,
    modifier: Modifier = Modifier,
    userActivityViewModel: UserActivityViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userActivityItems = userActivityViewModel.userActivity.collectAsLazyPagingItems()

    val isRefreshing by remember {
        derivedStateOf {
            userActivityItems.loadState.refresh is LoadState.Loading && userActivityItems.itemCount > 0
        }
    }

    LaunchedEffect(userId) {
        userActivityViewModel.setId(userId)
    }

    PullToRefreshCustomBox(
        isRefreshing = isRefreshing,
        modifier = modifier.fillMaxSize(),
        enabled = isRefreshEnabled,
        onRefresh = { userActivityItems.refresh() }
    ) {
        when(userActivityItems.loadState.refresh) {
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
                        message = stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { userActivityItems.refresh() }
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(
                        horizontal = horizontalPadding,
                        vertical = 8.dp
                    )
                ) {
                    items(
                        count = userActivityItems.itemCount,
                        span = { index ->
                            val item = userActivityItems.peek(index)

                            GridItemSpan(
                                currentLineSpan = when(item) {
                                    is ListActivity -> 1
                                    else -> maxLineSpan
                                }
                            )
                        }
                    ) { index ->
                        userActivityItems[index]?.let { activityItem ->
                            ActivityItem(
                                userActivity = activityItem,
                                onUserClick = { user ->
                                    navOptions.navigateToProfile(user)
                                },
                                onEntityClick = { entityType, id ->
                                    val detailsNavRoute = when (entityType) {
                                        EntityType.CHARACTER -> {
                                            DetailsNavRoute.CharacterDetails(id)
                                        }
                                        EntityType.PERSON -> {
                                            DetailsNavRoute.Staff(id)
                                        }
                                        EntityType.ANIME -> {
                                            DetailsNavRoute.AnimeDetails(id)
                                        }
                                        EntityType.MANGA, EntityType.RANOBE -> {
                                            DetailsNavRoute.MangaDetails(id)
                                        }
                                        EntityType.COMMENT -> {
                                            DetailsNavRoute.Comments(CommentsScreenMode.COMMENT, id, null)
                                        }
                                    }

                                    navOptions.navigateToDetails(detailsNavRoute)
                                },
                                onLinkClick = { link ->
                                    WebIntent.openUrlCustomTab(context, link)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    userActivityItems.apply {
                        when {
                            loadState.append is LoadState.Error -> {
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
                                            onButtonClick = { userActivityItems.retry() }
                                        )
                                    }
                                }
                            }
                            loadState.append is LoadState.Loading -> {
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
    }
}