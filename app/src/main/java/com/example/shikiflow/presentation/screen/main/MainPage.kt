package com.example.shikiflow.presentation.screen.main

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.shikiflow.domain.model.mapper.UserRateMapper
import com.example.shikiflow.domain.model.mapper.UserRateStatusConstants
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    mediaType: MediaType,
    isAtTop: Boolean,
    isAppBarVisible: Boolean,
    onMediaClick: (String, MediaType) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val tabs = UserRateStatusConstants.getStatusChips(mediaType)
    val pagerState = rememberPagerState { tabs.size }

    Column {
        MainTabRow(
            tabs = tabs.map { stringResource(id = it) },
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
            }, isAtTop = isAtTop
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val status = UserRateMapper.mapStringResToStatus(tabs[page])

            when(mediaType) {
                MediaType.ANIME -> {
                    AnimeTracksPage(
                        userStatus = status,
                        isAppBarVisible = isAppBarVisible,
                        onAnimeClick = { animeId ->
                            onMediaClick(animeId, mediaType)
                        }
                    )
                }
                MediaType.MANGA -> {
                    MangaTracksPage(
                        userStatus = status,
                        isAppBarVisible = isAppBarVisible,
                        onMangaClick = { mangaId ->
                            onMediaClick(mangaId, mediaType)
                        }
                    )
                }
            }
        }
    }
}
