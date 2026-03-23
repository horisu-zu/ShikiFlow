package com.example.shikiflow.presentation.viewmodel.anime.watch.episode

import com.example.shikiflow.domain.model.kodik.KodikEpisode

data class EpisodeUiState(
    val link: String? = null,
    val serialNum: Int? = null,
    val playerState: PlayerState = PlayerState(),
    val kodikEpisodeUiState: KodikEpisodeUiState = KodikEpisodeUiState(),
    val currentQuality: String? = null
)

data class KodikEpisodeUiState(
    val kodikEpisode: KodikEpisode? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

data class PlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = true,
    val duration: Long = 0L
)