package com.example.shikiflow.presentation.screen.more.notifications

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.more.notifications.NotificationTypeGroup.Companion.displayValue
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavOptions
import com.example.shikiflow.presentation.viewmodel.user.notifications.NotificationsViewModel
import kotlinx.coroutines.launch

@Composable
fun NotificationsScreen(
    navOptions: ProfileNavOptions,
    notificationsViewModel: NotificationsViewModel = hiltViewModel()
) {
    val uiState by notificationsViewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        snapAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )
    val scope = rememberCoroutineScope()
    var isAtTop by remember { mutableStateOf(true) }
    val pagerState = rememberPagerState(
        pageCount = { NotificationTypeGroup.entries.size }
    )

    val isScrolling by remember {
        derivedStateOf {
            !isAtTop || scrollBehavior.state.collapsedFraction == 1f
        }
    }

    val backgroundColor by animateColorAsState(
        targetValue = if(isScrolling) MaterialTheme.colorScheme.surfaceContainer
            else MaterialTheme.colorScheme.background
    )

    LaunchedEffect(pagerState.currentPage) {
        notificationsViewModel.setNotificationTypeGroup(
            typeGroup = NotificationTypeGroup.entries[pagerState.currentPage]
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        shape = RoundedCornerShape(percent = 24),
                        onClick = { navOptions.navigateBack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text(
                        text = "Notifications",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor,
                    scrolledContainerColor = backgroundColor
                ),
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding()
            )
        ) {
            PrimaryScrollableTabRow(
                selectedTabIndex = uiState.currentGroup.ordinal,
                edgePadding = 0.dp,
                containerColor = backgroundColor,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(
                                selectedTabIndex = pagerState.currentPage,
                                matchContentSize = true
                            ),
                        width = Dp.Unspecified,
                        shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp),
                    )
                },
                divider = {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceBright
                    )
                },
            ) {
                NotificationTypeGroup.entries.forEachIndexed { index, typeGroup ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = stringResource(id = typeGroup.displayValue()),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val lazyListState = rememberLazyListState()
                val notificationItems = notificationsViewModel.notificationItems[NotificationTypeGroup.entries[page]]
                    ?.collectAsLazyPagingItems() ?: return@HorizontalPager

                val isAtListTop by remember {
                    derivedStateOf {
                        lazyListState.firstVisibleItemIndex == 0 &&
                        lazyListState.firstVisibleItemScrollOffset == 0
                    }
                }

                LaunchedEffect(pagerState.currentPage, isAtListTop) {
                    if (pagerState.currentPage == page) {
                        isAtTop = isAtListTop
                    }
                }

                LaunchedEffect(pagerState.currentPage, notificationItems.loadState.refresh) {
                    if (pagerState.currentPage == NotificationTypeGroup.ALL.ordinal &&
                        notificationItems.loadState.refresh is LoadState.NotLoading &&
                        notificationItems.itemCount > 0
                    ) {
                        notificationsViewModel.resetUnreadCount()
                    }
                }

                LazyColumn(
                    state = lazyListState,
                    userScrollEnabled = notificationItems.loadState.refresh !is LoadState.Loading,
                    contentPadding = PaddingValues(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (notificationItems.loadState.refresh) {
                        is LoadState.Loading -> {
                            items(count = 24) { index ->
                                NotificationItemPlaceholder(
                                    itemIndex = index
                                )
                            }
                        }
                        is LoadState.Error -> {
                            val errorMessage = (notificationItems.loadState.refresh as LoadState.Error)
                                .error.message

                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ErrorItem(
                                        message = errorMessage ?: stringResource(id = R.string.common_error),
                                        buttonLabel = stringResource(id = R.string.common_retry),
                                        onButtonClick = { notificationItems.retry() }
                                    )
                                }
                            }
                        }
                        else -> {
                            items(
                                count = notificationItems.itemCount,
                                key = notificationItems.itemKey { it.id }
                            ) { index ->
                                val notificationItem = notificationItems[index] ?: return@items

                                NotificationItem(notification = notificationItem)
                            }

                            if (notificationItems.loadState.append is LoadState.Loading) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) { CircularProgressIndicator() }
                                }
                            } else if (notificationItems.loadState.append is LoadState.Error) {
                                val errorMessage = (notificationItems.loadState.append as LoadState.Error)
                                    .error.message

                                item {
                                    ErrorItem(
                                        message = errorMessage ?: stringResource(R.string.common_error),
                                        buttonLabel = stringResource(R.string.common_retry),
                                        showFace = false,
                                        onButtonClick = { notificationItems.retry() }
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