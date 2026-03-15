package com.example.shikiflow.presentation.viewmodel.anime.watch

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.shikiflow.domain.model.kodik.KodikEpisode
import com.example.shikiflow.domain.usecase.GetEpisodesUseCase
import com.example.shikiflow.presentation.screen.main.details.anime.watch.PlayerEvent
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EpisodeUiState(
    val playerState: PlayerState = PlayerState(),
    val episodeData: Resource<KodikEpisode> = Resource.Loading(),
    val currentQuality: String = ""
)

data class PlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = true,
    val duration: Long = 0L
)

@HiltViewModel
@UnstableApi
class EpisodeViewModel @Inject constructor(
    private val getEpisodesUseCase: GetEpisodesUseCase,
    @ApplicationContext context: Context
) : ViewModel(), PlayerEvent {

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    var currentKey: String = ""

    private val _currentPosition = MutableStateFlow<Long>(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _uiState = MutableStateFlow(EpisodeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        setupPlayerListener()
        startPositionUpdates()
    }

    fun getEpisode(link: String, serialNum: Int) {
        if(currentKey == "${link}_$serialNum") return

        getEpisodesUseCase(
            url = link,
            serialNum = serialNum
        ).onEach { result ->
            _uiState.update { state ->
                state.copy(
                    episodeData = result
                )
            }
            when(result) {
                is Resource.Loading -> {
                    Log.d("AnimeEpisodeViewModel","Loading episode with URL: $link")
                }
                is Resource.Success -> {
                    result.data?.let { kodikLink ->
                        val url = kodikLink.qualityLink.maxBy { linkMap ->
                            linkMap.key.toInt()
                        }.value

                        _uiState.update { state ->
                            state.copy(
                                currentQuality = extractQualityFromUrl(url)
                            )
                        }
                        currentKey = "${link}_$serialNum"

                        loadUrl(url, resetPosition = true)
                    }
                    Log.d("AnimeEpisodeViewModel","Result: ${result.data}")
                }
                is Resource.Error -> {
                    Log.d("AnimeEpisodeViewModel", "Error: ${result.message}")
                }
            }
        }.launchIn(viewModelScope)
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
        val links = _uiState.value.episodeData.data?.qualityLink ?: return
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