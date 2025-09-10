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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.shikiflow.domain.model.kodik.KodikLink
import com.example.shikiflow.presentation.viewmodel.anime.watch.PlayerViewModel
import com.example.shikiflow.utils.systemBarsVisibility
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
    onSeekToEpisode: (Int, Int) -> Unit,
    onQualityChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel = hiltViewModel(),
) {
    var isFit by remember { mutableStateOf(true) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var showControls by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val playerState by playerViewModel.playerState.collectAsStateWithLifecycle()
    val isLoading = playerState.isBuffering || isLoadingEpisode

    LaunchedEffect(exoPlayer) {
        playerViewModel.initPlayer(exoPlayer)
    }

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
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        showControls = !showControls
                    },
                    onDoubleTap = { offset ->
                        if (offset.x > containerSize.width / 2) {
                            playerViewModel.seekForward()
                        } else {
                            playerViewModel.seekBackward()
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
                episodesCount = episodesCount,
                currentQuality = currentQuality,
                translationGroup = translationGroup,
                qualityData = qualityData,
                onNavigateBack = onNavigateBack,
                onQualityChange = onQualityChange,
                onEpisodeChange = { episodeNum ->
                    onSeekToEpisode(episodeNum, 0)
                },
                onExpand = { value ->
                    isDropdownExpanded = value
                }
            )
        }

        PlayerControls(
            isPlaying = playerState.isPlaying,
            isLoading = isLoading,
            isPreviousAvailable = currentEpisode > 1,
            isNextAvailable = currentEpisode < episodesCount,
            onSeekToEpisode = { offset ->
                onSeekToEpisode(currentEpisode, offset)
            },
            onPlay = {
                if(playerState.isPlaying) playerViewModel.pause() else playerViewModel.play()
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
                duration = playerState.duration,
                currentProgress = playerState.currentPosition,
                isFit = isFit,
                onSeek = { position ->
                    playerViewModel.seekTo(position)
                },
                onSkip = {
                    playerViewModel.seekForward(87500L)
                },
                onResize = { isFit = !isFit }
            )
        }
    }
}