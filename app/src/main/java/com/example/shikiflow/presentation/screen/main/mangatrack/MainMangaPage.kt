package com.example.shikiflow.presentation.screen.main.mangatrack

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.data.mapper.UserRateMapper
import com.example.shikiflow.data.mapper.UserRateStatusConstants
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.screen.main.MainTabRow
import com.example.shikiflow.presentation.viewmodel.manga.MangaTracksViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMangaPage(
    mangaTracksViewModel: MangaTracksViewModel = hiltViewModel(),
    onMangaClick: (String) -> Unit
) {
    val pagerState = rememberPagerState { 6 }
    val coroutineScope = rememberCoroutineScope()
    val tabs = UserRateStatusConstants.getStatusChips(MediaType.MANGA)
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
            }
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val status = UserRateMapper.mapStringToStatus(tabs[page])
            val trackData = status?.let { mangaTracksViewModel.getMangaTracks(it) }?.collectAsLazyPagingItems()

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    coroutineScope.launch {
                        isRefreshing = true
                        delay(300)
                        isRefreshing = false
                    }
                }
            ) {
                MangaTracksPage(
                    trackItems = trackData,
                    onMangaClick = onMangaClick
                )
            }
        }
    }
}