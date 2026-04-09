package com.example.shikiflow.presentation.viewmodel.anime.tracks.search

import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.presentation.UiState

data class TracksUiState(
    val userId: Int? = null,
    val query: String = "",
    val userRateStatus: UserRateStatus? = null,
    val tracksItems: List<ShortUserMediaRate> = emptyList(),

    val isRefreshing: Boolean = false,
    override val isLoading: Boolean = true,
    override val errorMessage: String? = null
): UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
