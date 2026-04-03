package com.example.shikiflow.presentation.screen.main.details.anime.watch.player

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.example.shikiflow.presentation.screen.main.details.anime.watch.AnimeWatchNavOptions
import com.example.shikiflow.presentation.viewmodel.anime.watch.episode.EpisodeViewModel

@OptIn(UnstableApi::class)
@Composable
fun EpisodeScreen(
    title: String,
    playerNavigate: EpisodeMetadata,
    navOptions: AnimeWatchNavOptions,
    viewModel: EpisodeViewModel = hiltViewModel()
) {
    val player = viewModel.exoPlayer
    val episodeUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()

    LaunchedEffect(playerNavigate.link, playerNavigate.episodeNum) {
        viewModel.setEpisode(playerNavigate.link, playerNavigate.episodeNum)
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
            playerEvent = viewModel,
            title = title,
            currentPosition = currentPosition,
            episodeData = playerNavigate,
            episodesRange = playerNavigate.firstEpisode..playerNavigate.lastEpisode,
            currentQuality = episodeUiState.currentQuality ?: "",
            episodeUiState = episodeUiState.kodikEpisodeUiState,
            onSeekToEpisode = { episodeNum ->
                navOptions.navigateToEpisodeScreen(
                    playerNavigate = playerNavigate.copy(
                        episodeNum = episodeNum
                    )
                )
            },
            onNavigateBack = { navOptions.navigateBack() }
        )
    }
}