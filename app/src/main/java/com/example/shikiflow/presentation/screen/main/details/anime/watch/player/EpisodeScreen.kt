package com.example.shikiflow.presentation.screen.main.details.anime.watch.player

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.example.shikiflow.presentation.screen.main.details.anime.watch.AnimeWatchNavOptions
import com.example.shikiflow.presentation.viewmodel.anime.watch.EpisodeViewModel
import com.example.shikiflow.utils.Resource

@OptIn(UnstableApi::class)
@Composable
fun EpisodeScreen(
    playerNavigate: EpisodeMetadata,
    navOptions: AnimeWatchNavOptions,
    viewModel: EpisodeViewModel = hiltViewModel()
) {
    val player = viewModel.exoPlayer
    val episodeUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()

    val isLoadingEpisodeData by remember {
        derivedStateOf {
            episodeUiState.episodeData is Resource.Loading
        }
    }

    LaunchedEffect(playerNavigate.link, playerNavigate.serialNum) {
        viewModel.getEpisode(playerNavigate.link, playerNavigate.serialNum)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Player(
            player = player,
            playerState = episodeUiState.playerState,
            currentPosition = currentPosition,
            episodeData = playerNavigate,
            episodesCount = playerNavigate.episodesCount,
            currentQuality = episodeUiState.currentQuality,
            kodikEpisode = episodeUiState.episodeData.data,
            isLoadingEpisode = isLoadingEpisodeData,
            playerEvent = viewModel,
            onSeekToEpisode = { episodeNum ->
                navOptions.navigateToEpisodeScreen(
                    playerNavigate = playerNavigate.copy(
                        serialNum = episodeNum
                    )
                )
            },
            onNavigateBack = { navOptions.navigateBack() }
        )
    }
}