package com.example.shikiflow.presentation.screen.main.details.anime.watch.player

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.shikiflow.presentation.navigation.AppNavOptions
import com.example.shikiflow.presentation.viewmodel.anime.watch.AnimeEpisodeViewModel
import com.example.shikiflow.utils.Resource

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    title: String,
    link: String,
    translationGroup: String,
    serialNum: Int,
    episodesCount: Int,
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

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.release()
            animeEpisodeViewModel.clearMediaSource()
        }
    }

    LaunchedEffect(link, serialNum) {
        animeEpisodeViewModel.getEpisode(link, serialNum)
    }

    LaunchedEffect(mediaSource) {
        mediaSource?.let { source ->
            exoPlayer.setMediaSource(source)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Player(
            exoPlayer = exoPlayer,
            title = title,
            currentQuality = currentQuality,
            translationGroup = translationGroup,
            currentEpisode = serialNum,
            episodesCount = episodesCount,
            qualityData = episodeState.data,
            context = context,
            isLoadingEpisode = episodeState is Resource.Loading,
            onSeekToEpisode = { episodeNum, offset ->
                navOptions.navigateToPlayer(title, link, translationGroup, episodeNum, offset, episodesCount)
            },
            onQualityChange = { quality ->
                animeEpisodeViewModel.createMediaSource(quality)
            },
            onNavigateBack = { navOptions.navigateBack() },
            modifier = Modifier.padding(
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
            )
        )
    }
}