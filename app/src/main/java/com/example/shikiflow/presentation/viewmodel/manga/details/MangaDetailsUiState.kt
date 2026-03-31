package com.example.shikiflow.presentation.viewmodel.manga.details

import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.presentation.UiState

data class MangaDetailsUiState(
    val mediaId: Int? = null,
    val userId: Int? = null,
    val authType: AuthType? = null,
    val details: MediaDetails? = null,
    val mangaDexUiState: MangaDexUiState = MangaDexUiState(),
    val rateUpdateState: RateUpdateState = RateUpdateState.INITIAL,
    override val isLoading: Boolean = true,
    override val errorMessage: String? = null,
    val isRefreshing: Boolean = false
): UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}

data class MangaDexUiState(
    val mangaDexIds: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)