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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.user.social.Follower
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.social.Thread
import com.example.shikiflow.domain.model.user.social.ThreadComment
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.mappers.ProfileMapper.displayValue
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.main.details.common.ThreadItem
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavOptions
import com.example.shikiflow.presentation.viewmodel.user.social.UserSocialViewModel
import com.example.shikiflow.utils.WebIntent
import kotlinx.coroutines.launch

@Composable
fun SocialSection(
    userId: Int,
    socialCategories: List<SocialCategory>,
    horizontalPadding: Dp,
    navOptions: ProfileNavOptions,
    userSocialViewModel: UserSocialViewModel = hiltViewModel()
) {
    val params by userSocialViewModel.params.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(
        initialPage = params.currentCategory?.let { socialCategory ->
            socialCategories.indexOf(socialCategory)
        } ?: 0,
        pageCount = { socialCategories.size }
    )
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
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
                    val isSelected = params.currentCategory == socialCategory

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
        HorizontalPager(
            state = pagerState
        ) { page ->
            val category = socialCategories[page]
            val socialItems = userSocialViewModel.userSocialItems[category]
                ?.collectAsLazyPagingItems() ?: return@HorizontalPager

            when(socialItems.loadState.refresh) {
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
                            onButtonClick = { socialItems.refresh() }
                        )
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = when(category) {
                            SocialCategory.FOLLOWINGS, SocialCategory.FOLLOWERS -> GridCells.Adaptive(180.dp)
                            else -> GridCells.Fixed(1)
                        },
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
                        items(socialItems.itemCount) { index ->
                            socialItems[index]?.let { item ->
                                when(item) {
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
                                                    threadHeader = item.data,
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
                                                    threadHeader = item.thread,
                                                    id = item.thread.id
                                                )

                                                navOptions.navigateToDetails(navRoute)
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
                                                        DetailsNavRoute.Comments(
                                                            screenMode = CommentsScreenMode.COMMENT,
                                                            threadHeader = item.thread,
                                                            id = id
                                                        )
                                                    }
                                                }

                                                navOptions.navigateToDetails(detailsNavRoute)
                                            },
                                            onLinkClick = { link ->
                                                WebIntent.openUrlCustomTab(context, link)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        item(span = { GridItemSpan(maxLineSpan) }) {
                            if (socialItems.loadState.append is LoadState.Loading) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                            } else if (socialItems.loadState.append is LoadState.Error) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ErrorItem(
                                        message = stringResource(R.string.common_error),
                                        buttonLabel = stringResource(R.string.common_retry),
                                        onButtonClick = { socialItems.retry() }
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