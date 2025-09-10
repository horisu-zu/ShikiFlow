package com.example.shikiflow.presentation.viewmodel.anime.watch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor() : ViewModel() {

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    private var exoPlayer: ExoPlayer? = null

    fun initPlayer(player: ExoPlayer) {
        this.exoPlayer = player
        setupPlayerListeners()
        startPositionUpdates()
    }

    private fun setupPlayerListeners() {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val isBuffering = playbackState == Player.STATE_BUFFERING
                val duration = exoPlayer?.duration?.coerceAtLeast(0L) ?: 0L

                _playerState.value = _playerState.value.copy(
                    isBuffering = isBuffering,
                    duration = duration
                )
            }
        }
        exoPlayer?.addListener(listener)
    }

    private fun startPositionUpdates() {
        viewModelScope.launch {
            while (true) {
                exoPlayer?.let { player ->
                    _playerState.value = _playerState.value.copy(
                        currentPosition = player.currentPosition
                    )
                }
                delay(500L)
            }
        }
    }

    fun play() {
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
    }

    fun seekForward(milliseconds: Long = 15000L) {
        exoPlayer?.let { player ->
            val newPosition = player.currentPosition + milliseconds
            player.seekTo(newPosition)
        }
    }

    fun seekBackward(milliseconds: Long = 15000L) {
        exoPlayer?.let { player ->
            val newPosition = (player.currentPosition - milliseconds).coerceAtLeast(0L)
            player.seekTo(newPosition)
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer = null
    }
}

data class PlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = true,
    val duration: Long = 0L,
    val currentPosition: Long = 0L
)