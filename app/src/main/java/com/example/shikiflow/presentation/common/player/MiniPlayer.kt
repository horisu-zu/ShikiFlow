package com.example.shikiflow.presentation.common.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.ui.PlayerView
import com.example.shikiflow.presentation.screen.main.details.anime.watch.player.ControlButton
import com.example.shikiflow.presentation.screen.main.details.anime.watch.player.DurationBox
import com.example.shikiflow.presentation.screen.main.details.anime.watch.player.PlayerSlider
import com.example.shikiflow.presentation.viewmodel.anime.watch.episode.PlayerState
import kotlinx.coroutines.delay

@Composable
fun MiniPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val playerCache = LocalExoPlayerCache.current

    var progress by remember { mutableLongStateOf(0L) }
    var videoAspectRatio by remember { mutableFloatStateOf(16f / 9f) }
    var playerState by remember { mutableStateOf(PlayerState()) }

    val exoPlayer = remember(videoUrl) {
        playerCache.getOrCreate(videoUrl)
    }

    DisposableEffect(exoPlayer) {
        playerState = PlayerState(
            isPlaying = exoPlayer.isPlaying,
            isBuffering = exoPlayer.playbackState == Player.STATE_BUFFERING
        )

        val listener = object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                if (videoSize.width > 0 && videoSize.height > 0) {
                    videoAspectRatio = videoSize.width.toFloat() / videoSize.height.toFloat()
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playerState = playerState.copy(
                    isPlaying = isPlaying
                )
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                playerState = playerState.copy(
                    isBuffering = playbackState == Player.STATE_BUFFERING
                )
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.pause()
        }
    }

    LaunchedEffect(playerState.isPlaying) {
        while (playerState.isPlaying) {
            progress = exoPlayer.currentPosition

            delay(500L)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(videoAspectRatio)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                if(playerState.isPlaying) {
                    exoPlayer.pause()
                } else {
                    exoPlayer.play()
                }
            }
    ) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false
                }
            },
            update = { view ->
                view.player = exoPlayer
                if (exoPlayer.playbackState == Player.STATE_IDLE) {
                    exoPlayer.prepare()
                }
            },
            onRelease = { playerView ->
                playerView.player = null
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )

        PlayerControls(
            playerState = playerState,
            onPlayClick = { exoPlayer.play() },
            modifier = Modifier.align(Alignment.Center)
        )

        AnimatedVisibility(
            visible = !playerState.isPlaying && !playerState.isBuffering,
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
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            PlayerBottomComponent(
                currentProgress = progress,
                duration = exoPlayer.duration,
                onSeek = { positionMs ->
                    progress = positionMs
                    exoPlayer.seekTo(positionMs)
                },
                thumbSize = 12.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PlayerControls(
    playerState: PlayerState,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !playerState.isPlaying || playerState.isBuffering,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        if(playerState.isBuffering) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp)
            )
        } else {
            ControlButton(
                visible = !playerState.isPlaying,
                onClick = onPlayClick
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Arrow",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Composable
private fun PlayerBottomComponent(
    currentProgress: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    thumbSize: Dp,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        PlayerSlider(
            duration = duration,
            currentProgress = currentProgress,
            onSeek = { positionMs ->
                onSeek(positionMs)
            },
            thumbSize = thumbSize
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = thumbSize / 2),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DurationBox(currentProgress)
            DurationBox(duration)
        }
    }
}