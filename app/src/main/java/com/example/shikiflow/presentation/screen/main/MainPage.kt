package com.example.shikiflow.presentation.screen.main

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shikiflow.data.mapper.UserRateMapper
import com.example.shikiflow.presentation.viewmodel.AnimeTracksViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    trackViewModel: AnimeTracksViewModel = hiltViewModel(),
    onTabSelected: (Int) -> Unit
) {
    val pagerState = rememberPagerState { 6 }
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf(
        "Watching", "Planned", "Watched",
        "Rewatching", "On Hold", "Dropped"
    )
    var isRefreshing by remember { mutableStateOf(false) }

    Column {
        MainTabRow(
            tabs = tabs,
            selectedTab = pagerState.currentPage,
            onTabSelected = {
                onTabSelected(it)
                coroutineScope.launch {
                    pagerState.animateScrollToPage(
                        page = it,
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            },
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val status = UserRateMapper.mapStatus(tabs[page])

            LaunchedEffect(status) {
                status?.let {
                    trackViewModel.loadAnimeTracks(it)
                }
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    status?.let {
                        coroutineScope.launch {
                            try {
                                Log.d("PullToRefresh", "Refreshing...")
                                isRefreshing = true
                                delay(300)
                                trackViewModel.loadAnimeTracks(it, isRefresh = true)
                            } finally {
                                Log.d("PullToRefresh", "Refresh completed")
                                isRefreshing = false
                            }
                        }
                    }
                }
            ) {
                status?.let {
                    AnimeTracksPage(
                        trackViewModel = trackViewModel,
                        status = it
                    )
                }
            }
        }
    }
}
