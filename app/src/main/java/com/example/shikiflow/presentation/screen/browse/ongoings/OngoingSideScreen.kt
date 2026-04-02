package com.example.shikiflow.presentation.screen.browse.ongoings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.browse.BrowseType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.mappers.BrowseTypeMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.DateMapper.displayValue
import com.example.shikiflow.presentation.viewmodel.browse.calendar.OngoingsCalendarViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OngoingSideScreen(
    browseType: BrowseType,
    onNavigate: (Int) -> Unit,
    onBackNavigate: () -> Unit,
    ongoingsCalendarViewModel: OngoingsCalendarViewModel = hiltViewModel()
) {
    val params by ongoingsCalendarViewModel.params.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        snapAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )
    val isAtTop by remember {
        derivedStateOf {
            scrollBehavior.state.collapsedFraction < 1f
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = browseType.displayValue())
                    )
                },
                actions = {
                    FilterChip(
                        selected = params.onList,
                        label = {
                            Text(
                                text = "On My List"
                            )
                        },
                        onClick = { ongoingsCalendarViewModel.setOnList(!params.onList) },
                        leadingIcon = if(params.onList) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Done icon",
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else { null }
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
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        OngoingSideScreenContent(
            isAtTop = isAtTop,
            onNavigate = onNavigate,
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding()
            )
        )
    }
}

@Composable
private fun OngoingSideScreenContent(
    isAtTop: Boolean,
    onNavigate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    ongoingsCalendarViewModel: OngoingsCalendarViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(
        initialPage = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .dayOfWeek
            .ordinal,
        pageCount = { DayOfWeek.entries.size }
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        ongoingsCalendarViewModel.setCurrentDay(
            currentDay = DayOfWeek.entries[pagerState.currentPage]
        )
    }

    Column(modifier = modifier) {
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = if(isAtTop) MaterialTheme.colorScheme.background
                else MaterialTheme.colorScheme.surfaceContainer,
            edgePadding = 0.dp,
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
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            DayOfWeek.entries.forEachIndexed { index, dayOfWeek ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(id = dayOfWeek.displayValue()),
                            style = MaterialTheme.typography.bodyMedium,
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
            val calendarItems = ongoingsCalendarViewModel.calendarItems[DayOfWeek.entries[page]]
                ?.collectAsLazyPagingItems() ?: return@HorizontalPager

            when(calendarItems.loadState.refresh) {
                is LoadState.Loading -> {
                    Box(
                        modifier = modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
                is LoadState.Error -> {
                    val errorMessage = (calendarItems.loadState.refresh as LoadState.Error)
                        .error.localizedMessage

                    Box(
                        modifier = modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = errorMessage ?: stringResource(id = R.string.common_error),
                            buttonLabel = stringResource(id = R.string.common_retry),
                            onButtonClick = { calendarItems.retry() }
                        )
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(240.dp),
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(calendarItems.itemCount) { index ->
                            calendarItems[index]?.let { airingAnime ->
                                AiringAnimeItem(
                                    airingAnime = airingAnime,
                                    onClick = onNavigate
                                )
                            }
                        }

                        calendarItems.apply {
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
                                        message = stringResource(R.string.common_error),
                                        showFace = false,
                                        buttonLabel = stringResource(R.string.common_retry),
                                        onButtonClick = { calendarItems.retry() }
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