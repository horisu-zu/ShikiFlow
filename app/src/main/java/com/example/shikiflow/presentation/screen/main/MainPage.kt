package com.example.shikiflow.presentation.screen.main

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.data.mapper.UserRateMapper
import com.example.shikiflow.data.mapper.UserRateStatusConstants
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.presentation.viewmodel.anime.AnimeTracksViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    rootNavController: NavController,
    pagerState: PagerState,
    trackViewModel: AnimeTracksViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val tabs = UserRateStatusConstants.getStatusChips(MediaType.ANIME)
    var isRefreshing by remember { mutableStateOf(false) }

    Column {
        MainTabRow(
            tabs = tabs,
            selectedTab = pagerState.currentPage,
            onTabSelected = {
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
            val status = UserRateMapper.mapStringToStatus(tabs[page])

            val trackItems = status?.let { trackViewModel.getAnimeTracks(it) }?.collectAsLazyPagingItems()

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    status?.let {
                        coroutineScope.launch {
                            try {
                                Log.d("PullToRefresh", "Refreshing...")
                                isRefreshing = true
                                trackItems?.refresh()
                                delay(300)
                            } finally {
                                Log.d("PullToRefresh", "Refresh completed")
                                isRefreshing = false
                            }
                        }
                    }
                }
            ) {
                trackItems?.let { items ->
                    AnimeTracksPage(
                        rootNavController = rootNavController,
                        trackItems = items,
                        tracksViewModel = trackViewModel
                    )
                }
            }
        }
    }
}
