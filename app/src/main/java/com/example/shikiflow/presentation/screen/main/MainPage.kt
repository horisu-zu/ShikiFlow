package com.example.shikiflow.presentation.screen.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    userId: String?,
    mediaType: MediaType,
    isAtTop: Boolean,
    isAppBarVisible: Boolean,
    onMediaClick: (Int, MediaType) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val tabs = UserRateStatus.entries.filter { it != UserRateStatus.UNKNOWN }.toList()
    val pagerState = rememberPagerState { tabs.size }

    Column {
        MainTabRow(
            tabs = tabs,
            mediaType = mediaType,
            selectedTab = pagerState.currentPage,
            onTabSelected = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(
                        page = it,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                }
            },
            isAtTop = isAtTop
        )
        userId?.let {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                AnimatedContent(
                    targetState = mediaType
                ) { type ->
                    when (type) {
                        MediaType.ANIME -> {
                            AnimeTracksPage(
                                userStatus = UserRateStatus.entries[page],
                                userId = userId,
                                isAppBarVisible = isAppBarVisible,
                                onAnimeClick = { animeId ->
                                    onMediaClick(animeId, mediaType)
                                }
                            )
                        }

                        MediaType.MANGA -> {
                            MangaTracksPage(
                                userStatus = UserRateStatus.entries[page],
                                userId = userId,
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
    }
}
