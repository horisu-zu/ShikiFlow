package com.example.shikiflow.presentation.screen.main

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.domain.model.mapper.UserRateMapper
import com.example.shikiflow.domain.model.mapper.UserRateStatusConstants
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.manga.MangaTrack
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.PullToRefreshCustomBox
import com.example.shikiflow.presentation.viewmodel.anime.AnimeTracksViewModel
import com.example.shikiflow.presentation.viewmodel.manga.MangaTracksViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    mediaType: MediaType,
    isAtTop: Boolean,
    isAppBarVisible: Boolean,
    animeTrackViewModel: AnimeTracksViewModel = hiltViewModel(),
    mangaTrackViewModel: MangaTracksViewModel = hiltViewModel(),
    onAnimeClick: (String) -> Unit,
    onMangaClick: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val tabs = UserRateStatusConstants.getStatusChips(mediaType)
    val pagerState = rememberPagerState { tabs.size }
    var isRefreshing by remember { mutableStateOf(false) }

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
            val mediaTrackItems = status?.let { statusEnum ->
                when(mediaType) {
                    MediaType.ANIME -> MediaTrackItems.AnimeItems(
                        animeTrackViewModel.getAnimeTracks(statusEnum).collectAsLazyPagingItems()
                    )
                    MediaType.MANGA -> MediaTrackItems.MangaItems(
                        mangaTrackViewModel.getMangaTracks(statusEnum).collectAsLazyPagingItems()
                    )
                }
            }

            PullToRefreshCustomBox(
                isRefreshing = isRefreshing,
                enabled = isAppBarVisible,
                onRefresh = {
                    status?.let {
                        coroutineScope.launch {
                            try {
                                Log.d("PullToRefresh", "Refreshing...")
                                isRefreshing = true
                                mediaTrackItems?.let { trackItems ->
                                    when(trackItems) {
                                        is MediaTrackItems.AnimeItems -> trackItems.items?.refresh()
                                        is MediaTrackItems.MangaItems -> trackItems.items?.refresh()
                                    }
                                }
                                delay(100)
                            } finally {
                                Log.d("PullToRefresh", "Refresh completed")
                                isRefreshing = false
                            }
                        }
                    }
                }
            ) {
                mediaTrackItems?.let { trackItems ->
                    when(trackItems) {
                        is MediaTrackItems.AnimeItems -> {
                            AnimeTracksPage(
                                trackItems = trackItems.items,
                                tracksViewModel = animeTrackViewModel,
                                onAnimeClick = onAnimeClick
                            )
                        }
                        is MediaTrackItems.MangaItems -> {
                            MangaTracksPage(
                                trackItems = trackItems.items,
                                onMangaClick = onMangaClick
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class MediaTrackItems {
    data class AnimeItems(val items: LazyPagingItems<AnimeTrack>?) : MediaTrackItems()
    data class MangaItems(val items: LazyPagingItems<MangaTrack>?) : MediaTrackItems()
}
