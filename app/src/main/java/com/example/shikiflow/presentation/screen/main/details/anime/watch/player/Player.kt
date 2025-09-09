package com.example.shikiflow.presentation.screen.main.details.anime.watch.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.shikiflow.domain.model.kodik.KodikLink
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun Player(
    exoPlayer: ExoPlayer,
    title: String,
    currentQuality: String,
    translationGroup: String,
    currentEpisode: Int,
    episodesCount: Int,
    qualityData: KodikLink?,
    context: Context,
    isLoadingEpisode: Boolean,
    onSeekToEpisode: (Int) -> Unit,
    onQualityChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFit by remember { mutableStateOf(true) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var duration by remember { mutableLongStateOf(0L) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var isPlaying by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(false) }

    val isLoading = isBuffering || isLoadingEpisode

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isMediaPlaying: Boolean) {
                isPlaying = isMediaPlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
                duration = exoPlayer.duration.takeIf { it != C.TIME_UNSET } ?: 0L
            }
        }
        exoPlayer.addListener(listener)

        onDispose { exoPlayer.removeListener(listener) }
    }

    LaunchedEffect(isPlaying, showControls) {
        while (isPlaying) {
            currentPosition = exoPlayer.currentPosition
            val updateInterval = if (showControls) 250L else 500L
            delay(updateInterval)
        }
    }

    LaunchedEffect(showControls) {
        if(!isLoading) {
            delay(3000)
            showControls = false
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
            .onSizeChanged { containerSize = it }
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        showControls = !showControls
                    },
                    onDoubleTap = { offset ->
                        if(offset.x > containerSize.width / 2) {
                            exoPlayer.seekTo(exoPlayer.currentPosition + 15000L)
                        } else {
                            exoPlayer.seekTo(exoPlayer.currentPosition - 15000L)
                        }
                    }
                )
            }
    ) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = if(isFit) AspectRatioFrameLayout.RESIZE_MODE_FIT
                        else AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            },
            update = { view ->
                view.resizeMode = if (isFit) {
                    AspectRatioFrameLayout.RESIZE_MODE_FIT
                } else {
                    AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            }, modifier = Modifier.fillMaxSize()
        )

        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn() + slideInVertically(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMedium,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                ),
                initialOffsetY = { offset -> -offset / 2 }
            ),
            exit = fadeOut() + slideOutVertically(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMedium,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                ),
                targetOffsetY = { offset -> -offset / 2 }
            ),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            PlayerTopComponent(
                title = title,
                episodeNum = currentEpisode,
                currentQuality = currentQuality,
                translationGroup = translationGroup,
                qualityData = qualityData,
                onNavigateBack = onNavigateBack,
                onQualityChange = onQualityChange
            )
        }

        PlayerControls(
            isPlaying = isPlaying,
            isLoading = isLoading,
            isPreviousAvailable = currentEpisode > 1,
            isNextAvailable = currentEpisode < episodesCount,
            onSeekToEpisode = onSeekToEpisode,
            onPlay = {
                if(isPlaying) exoPlayer.pause() else exoPlayer.play()
            },
            showControls = showControls,
            modifier = Modifier.align(Alignment.Center)
        )

        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn() + slideInVertically(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMedium,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                ),
                initialOffsetY = { offset -> offset / 2 }
            ),
            exit = fadeOut() + slideOutVertically(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMedium,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                ),
                targetOffsetY = { offset -> offset / 2 }
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            PlayerBottomComponent(
                duration = duration,
                currentProgress = currentPosition,
                isFit = isFit,
                onSeek = { position ->
                    exoPlayer.seekTo(position)
                },
                onSkip = {
                    exoPlayer.seekTo(exoPlayer.currentPosition + 85000L)
                },
                onResize = { isFit = !isFit }
            )
        }
    }
}