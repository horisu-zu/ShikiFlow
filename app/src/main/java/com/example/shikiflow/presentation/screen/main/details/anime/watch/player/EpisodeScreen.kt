package com.example.shikiflow.presentation.screen.main.details.anime.watch.player

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.shikiflow.presentation.navigation.AppNavOptions
import com.example.shikiflow.presentation.viewmodel.anime.watch.AnimeEpisodeViewModel
import com.example.shikiflow.utils.Resource

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    playerNavigate: PlayerNavigate,
    navOptions: AppNavOptions,
    animeEpisodeViewModel: AnimeEpisodeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val episodeState by animeEpisodeViewModel.episodeState.collectAsStateWithLifecycle()
    val mediaSource by animeEpisodeViewModel.mediaSource.collectAsStateWithLifecycle()

    val currentQuality = animeEpisodeViewModel.currentQuality

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    val isLoadingEpisodeData by remember {
        derivedStateOf {
            episodeState is Resource.Loading
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.release()
            animeEpisodeViewModel.clearMediaSource()
        }
    }

    LaunchedEffect(playerNavigate.link, playerNavigate.serialNum) {
        animeEpisodeViewModel.getEpisode(playerNavigate.link, playerNavigate.serialNum)
    }

    LaunchedEffect(mediaSource) {
        mediaSource?.let { source ->
            exoPlayer.setMediaSource(source)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black)
    ) {
        Player(
            exoPlayer = exoPlayer,
            title = playerNavigate.title,
            currentQuality = currentQuality,
            translationGroup = playerNavigate.translationGroup,
            currentEpisode = playerNavigate.serialNum,
            episodesCount = playerNavigate.episodesCount,
            qualityData = episodeState.data,
            context = context,
            isLoadingEpisode = isLoadingEpisodeData,
            onSeekToEpisode = { episodeNum ->
                navOptions.navigateToPlayer(
                    playerNavigate = playerNavigate.copy(
                        serialNum = episodeNum
                    )
                )
            },
            onQualityChange = { quality ->
                animeEpisodeViewModel.createMediaSource(quality)
            },
            onNavigateBack = { navOptions.navigateBack() }
        )
    }
}