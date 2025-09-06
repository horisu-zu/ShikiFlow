package com.example.shikiflow.presentation.screen.main.details.anime.watch

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.navigation.AppNavOptions
import com.example.shikiflow.presentation.viewmodel.anime.watch.AnimeEpisodeViewModel
import com.example.shikiflow.utils.Resource

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    link: String,
    serialNum: Int,
    navOptions: AppNavOptions,
    animeEpisodeViewModel: AnimeEpisodeViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val episodeState by animeEpisodeViewModel.episodeState.collectAsStateWithLifecycle()
    val mediaSource by animeEpisodeViewModel.mediaSource.collectAsStateWithLifecycle()

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.release()
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
        when(episodeState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is Resource.Success -> {
                AndroidView(
                    factory = {
                        PlayerView(context).apply {
                            player = exoPlayer
                            useController = true
                        }
                    }, modifier = Modifier.fillMaxSize().padding(
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                    )
                )
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { animeEpisodeViewModel.getEpisode(link, serialNum) }
                    )
                }
            }
        }
    }
}