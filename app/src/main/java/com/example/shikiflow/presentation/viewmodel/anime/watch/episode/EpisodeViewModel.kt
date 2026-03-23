package com.example.shikiflow.presentation.viewmodel.anime.watch.episode

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.shikiflow.domain.usecase.GetEpisodesUseCase
import com.example.shikiflow.presentation.screen.main.details.anime.watch.PlayerEvent
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
@UnstableApi
class EpisodeViewModel @Inject constructor(
    private val getEpisodesUseCase: GetEpisodesUseCase,
    @ApplicationContext context: Context
) : ViewModel(), PlayerEvent {

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    private val _currentPosition = MutableStateFlow<Long>(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _uiState = MutableStateFlow(EpisodeUiState())
    val uiState = _uiState.asStateFlow()

    fun setEpisode(link: String, serialNum: Int) {
        _uiState.update { state ->
            state.copy(
                link = link,
                serialNum = serialNum
            )
        }
    }

    init {
        setupPlayerListener()
        startPositionUpdates()

        _uiState
            .filter { state ->
                state.link != null && state.serialNum != null
            }
            .distinctUntilChanged { old, new ->
                old.link == new.link &&
                old.serialNum == new.serialNum &&
                !new.kodikEpisodeUiState.isRefreshing
            }
            .flatMapLatest { state ->
                getEpisodesUseCase(
                    url = state.link!!,
                    serialNum = state.serialNum!!
                )
            }.onEach { result ->
                _uiState.update { state ->
                    when(result) {
                        DataResult.Loading -> {
                            state.copy(
                                kodikEpisodeUiState = state.kodikEpisodeUiState.copy(
                                    isLoading = true,
                                    isRefreshing = false,
                                    errorMessage = null
                                )
                            )
                        }
                        is DataResult.Error -> {
                            state.copy(
                                kodikEpisodeUiState = state.kodikEpisodeUiState.copy(
                                    errorMessage = result.message,
                                    isLoading = false
                                )
                            )
                        }
                        is DataResult.Success -> {
                            state.copy(
                                kodikEpisodeUiState = state.kodikEpisodeUiState.copy(
                                    kodikEpisode = result.data,
                                    isLoading = false
                                )
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)

        viewModelScope.launch {
            _uiState
                .filter { state ->
                    state.kodikEpisodeUiState.kodikEpisode != null
                }
                .distinctUntilChanged { old, new ->
                    old.link == new.link && old.serialNum == new.serialNum
                }
                .collectLatest { state ->
                    val url = state.kodikEpisodeUiState.kodikEpisode!!.qualityLink.maxBy { linkMap ->
                        linkMap.key.toInt()
                    }.value

                    _uiState.update { state ->
                        state.copy(
                            currentQuality = extractQualityFromUrl(url)
                        )
                    }

                    loadUrl(url, resetPosition = true)
                }
        }
    }

    private fun loadUrl(
        url: String,
        resetPosition: Boolean
    ) {
        val factory = DefaultHttpDataSource.Factory()
        val source = when {
            url.contains("m3u8") -> HlsMediaSource.Factory(factory)
                .createMediaSource(MediaItem.fromUri(url))
            else -> ProgressiveMediaSource.Factory(factory)
                .createMediaSource(MediaItem.fromUri(url))
        }

        exoPlayer.setMediaSource(source, resetPosition)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun onQualityChange(quality: String) {
        val links = _uiState.value.kodikEpisodeUiState.kodikEpisode?.qualityLink ?: return
        val url = links[quality] ?: return

        _uiState.update { state ->
            state.copy(currentQuality = quality)
        }

        loadUrl(url, resetPosition = false)
    }

    private fun setupPlayerListener() {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.update { state ->
                    state.copy(
                        playerState = state.playerState.copy(isPlaying = isPlaying)
                    )
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val isBuffering = playbackState == Player.STATE_BUFFERING
                val duration = exoPlayer.duration.coerceAtLeast(0L)

                _uiState.update { state ->
                    state.copy(
                        playerState = state.playerState.copy(
                            isBuffering = isBuffering,
                            duration = duration
                        )
                    )
                }
            }
        }
        exoPlayer.addListener(listener)
    }

    private fun startPositionUpdates() {
        viewModelScope.launch {
            while (true) {
                exoPlayer.let { player ->
                    _currentPosition.update { player.currentPosition }
                }
                delay(500L)
            }
        }
    }

    override fun onPlayToggle() {
        when(exoPlayer.isPlaying) {
            true -> exoPlayer.pause()
            false -> exoPlayer.play()
        }
    }

    override fun onSeekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
    }

    override fun onSeek(milliseconds: Long) {
        exoPlayer.let { player ->
            val newPosition = player.currentPosition + milliseconds
            player.seekTo(newPosition)
        }
    }

    private fun extractQualityFromUrl(videoUrl: String): String {
        return videoUrl
            .substringAfterLast("/")
            .substringBefore(":")
            .substringBefore(".mp4")
            .takeIf { it.matches(Regex("\\d+p?")) } ?: "unknown"
    }

    override fun onCleared() {
        super.onCleared()

        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        exoPlayer.release()
    }
}