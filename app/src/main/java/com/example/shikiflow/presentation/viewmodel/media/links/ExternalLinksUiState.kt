package com.example.shikiflow.presentation.viewmodel.media.links

import com.example.shikiflow.domain.model.media_details.ExternalLinkData
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.UiState

data class ExternalLinksUiState(
    val mediaId: Int? = null,
    val mediaType: MediaType? = null,
    val links: List<ExternalLinkData> = emptyList(),

    override val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    override val errorMessage: String? = null
): UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}