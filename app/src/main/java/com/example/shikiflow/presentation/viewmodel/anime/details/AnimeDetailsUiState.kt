package com.example.shikiflow.presentation.viewmodel.anime.details

import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.presentation.UiState

data class AnimeDetailsUiState(
    val mediaId: Int? = null,
    val userId: Int? = null,
    val authType: AuthType? = null,

    val details: MediaDetails? = null,
    val rateUpdateState: RateUpdateState = RateUpdateState.INITIAL,
    val isRefreshing: Boolean = false,
    override val isLoading: Boolean = true,
    override val errorMessage: String? = null
): UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}