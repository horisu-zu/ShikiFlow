package com.example.shikiflow.presentation.screen.main.details.anime.watch.player

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.shikiflow.presentation.screen.main.details.anime.watch.PlayerEvent
import com.example.shikiflow.presentation.viewmodel.anime.watch.episode.KodikEpisodeUiState
import com.example.shikiflow.presentation.viewmodel.anime.watch.episode.PlayerState
import com.example.shikiflow.utils.systemBarsVisibility
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun Player(
    player: ExoPlayer?,
    playerState: PlayerState,
    playerEvent: PlayerEvent,
    title: String,
    currentPosition: Long,
    episodeData: EpisodeMetadata,
    episodesRange: IntRange,
    currentQuality: String,
    episodeUiState: KodikEpisodeUiState,
    onSeekToEpisode: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var isFit by rememberSaveable { mutableStateOf(true) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var showControls by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val isLoading = player?.playbackState == Player.STATE_BUFFERING || episodeUiState.isLoading

    LaunchedEffect(showControls, playerState.isPlaying, isDropdownExpanded) {
        delay(3000)
        if (playerState.isPlaying && !isDropdownExpanded) {
            showControls = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .systemBarsVisibility(showControls)
            .onSizeChanged { containerSize = it }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        showControls = !showControls
                    },
                    onDoubleTap = { offset ->
                        val milliseconds = if (offset.x > containerSize.width / 2) {
                            15000L
                        } else -15000L
                        playerEvent.onSeek(milliseconds)
                    }
                )
            }
    ) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    useController = false
                }
            },
            update = { view ->
                view.player = player
                view.resizeMode = if (isFit) {
                    AspectRatioFrameLayout.RESIZE_MODE_FIT
                } else {
                    AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            },
            onRelease = { view ->
                view.player = null
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
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
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            PlayerTopComponent(
                title = title,
                episodeNum = episodeData.episodeNum,
                episodesList = episodesRange.toList(),
                currentQuality = currentQuality,
                translationGroup = episodeData.translationGroup,
                qualityData = episodeUiState.kodikEpisode?.qualityLink?.keys?.toList(),
                onNavigateBack = onNavigateBack,
                onQualityChange = { quality ->
                    playerEvent.onQualityChange(quality)
                },
                onEpisodeChange = { episodeNum ->
                    onSeekToEpisode(episodeNum)
                },
                onExpand = { value ->
                    isDropdownExpanded = value
                }
            )
        }

        PlayerControls(
            isPlaying = playerState.isPlaying,
            isLoading = isLoading,
            isPreviousAvailable = episodeData.episodeNum > 1,
            isNextAvailable = episodeData.episodeNum < episodesRange.last,
            onSeekToEpisode = { offset ->
                onSeekToEpisode(episodeData.episodeNum + offset)
            },
            onPlay = { playerEvent.onPlayToggle() },
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
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            PlayerBottomComponent(
                duration = playerState.duration,
                currentPosition = currentPosition,
                playerEvent = playerEvent,
                opTimeCode = episodeUiState.kodikEpisode?.opTimeCode,
                isFit = isFit,
                onResize = { isFit = !isFit },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}