package com.example.shikiflow.presentation.screen.more.profile.social

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.domain.model.user.social.Follower
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.social.Thread
import com.example.shikiflow.domain.model.user.social.ThreadComment
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.PullToRefreshCustomBox
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.displayValue
import com.example.shikiflow.presentation.common.player.LocalExoPlayerCache
import com.example.shikiflow.presentation.common.player.rememberExoPlayerCache
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.main.details.common.ThreadItem
import com.example.shikiflow.presentation.screen.main.details.common.ThreadItemPlaceholder
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavOptions
import com.example.shikiflow.presentation.viewmodel.user.social.UserSocialViewModel
import com.example.shikiflow.utils.LazyListUtils.onBottomReached
import kotlinx.coroutines.launch

@Composable
fun SocialSection(
    userId: Int,
    socialCategories: List<SocialCategory>,
    isRefreshEnabled: Boolean,
    horizontalPadding: Dp,
    onRefresh: () -> Unit,
    navOptions: ProfileNavOptions,
    userSocialViewModel: UserSocialViewModel = hiltViewModel()
) {
    val uiState by userSocialViewModel.uiState.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(
        initialPage = uiState.currentCategory?.let { socialCategory ->
            socialCategories.indexOf(socialCategory).let { index ->
                if(index != -1) index else 0
            }
        } ?: 0,
        pageCount = { socialCategories.size }
    )
    val scope = rememberCoroutineScope()
    val exoPlayerCache = rememberExoPlayerCache()
    val resources = LocalResources.current

    LaunchedEffect(userId) {
        userSocialViewModel.setUserId(userId)
    }

    LaunchedEffect(pagerState.currentPage) {
        userSocialViewModel.setCategory(
            socialCategory = socialCategories[pagerState.currentPage]
        )
    }

    Scaffold(
        topBar = {
            SnapFlingLazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(socialCategories) { index, socialCategory ->
                    val isSelected = uiState.currentCategory == socialCategory

                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    page = index,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    )
                                )
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(id = socialCategory.displayValue())
                            )
                        },
                        leadingIcon = if(isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null
                                )
                            }
                        } else { null }
                    )
                }
            }
        }
    ) { paddingValues ->
        CompositionLocalProvider(LocalExoPlayerCache provides exoPlayerCache) {
            HorizontalPager(
                state = pagerState
            ) { page ->
                val category = socialCategories[page]
                val categoryState = uiState.categories[category] ?: return@HorizontalPager
                val lazyGridState = rememberLazyGridState()

                if (!categoryState.isLoading && !categoryState.isRefreshing) {
                    lazyGridState.onBottomReached(
                        buffer = 6,
                        onLoadMore = { userSocialViewModel.onLoadMore(category) }
                    )
                }

                if (categoryState.errorMessage != null && categoryState.items.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = stringResource(R.string.common_error),
                            buttonLabel = stringResource(R.string.common_retry),
                            onButtonClick = { userSocialViewModel.refresh(category) }
                        )
                    }
                } else {
                    PullToRefreshCustomBox(
                        enabled = isRefreshEnabled,
                        isRefreshing = categoryState.isRefreshing,
                        onRefresh = {
                            onRefresh()
                            userSocialViewModel.refresh(category)
                        }
                    ) {
                        LazyVerticalGrid(
                            state = lazyGridState,
                            columns = when (category) {
                                SocialCategory.FOLLOWINGS,
                                SocialCategory.FOLLOWERS -> GridCells.Adaptive(180.dp)
                                else -> GridCells.Fixed(1)
                            },
                            userScrollEnabled = !(categoryState.isLoading && categoryState.items.isEmpty()),
                            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(
                                horizontal = horizontalPadding,
                                vertical = 8.dp
                            ),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = paddingValues.calculateTopPadding())
                        ) {
                            items(categoryState.items) { item ->
                                when (item) {
                                    is Follower -> {
                                        UserSocialItem(
                                            user = item.data,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable {
                                                    navOptions.navigateToProfile(item.data)
                                                }
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                    is Thread -> {
                                        ThreadItem(
                                            threadData = item.data,
                                            resources = resources,
                                            onThreadClick = { id ->
                                                val navRoute = DetailsNavRoute.Comments(
                                                    screenMode = CommentsScreenMode.TOPIC,
                                                    id = id
                                                )

                                                navOptions.navigateToDetails(navRoute)
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    is ThreadComment -> {
                                        ThreadCommentItem(
                                            threadComment = item,
                                            onThreadClick = {
                                                val navRoute = DetailsNavRoute.Comments(
                                                    screenMode = CommentsScreenMode.TOPIC,
                                                    id = item.thread.id
                                                )

                                                navOptions.navigateToDetails(navRoute)
                                            },
                                            onLikeToggle = { commentId ->
                                                userSocialViewModel.toggleCommentLike(commentId)
                                            },
                                            onEntityClick = { entityType, id ->
                                                navOptions.navigateByEntity(entityType, id)
                                            }
                                        )
                                    }
                                }
                            }

                            if (categoryState.isLoading) {
                                items(24) { index ->
                                    when (category) {
                                        SocialCategory.THREADS -> {
                                            ThreadItemPlaceholder(itemIndex = index)
                                        }
                                        SocialCategory.COMMENTS -> {
                                            ThreadCommentItemPlaceholder(itemIndex = index)
                                        }
                                        else -> {
                                            UserSocialItemPlaceholder(
                                                itemIndex = index,
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp,
                                                    vertical = 6.dp
                                                )
                                            )
                                        }
                                    }
                                }
                            } else if (categoryState.errorMessage != null) {
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
                                            onButtonClick = { userSocialViewModel.retry(category) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}