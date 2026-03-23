package com.example.shikiflow.presentation.viewmodel.user.compare

import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.ComparisonType
import com.example.shikiflow.domain.model.user.MediaComparison

data class CompareScreenUiState(
    val currentUserId: String? = null,
    val targetUserId: String? = null,
    val mediaType: MediaType? = null,
    val mediaUiState: Map<MediaType, CompareMediaUiState> =
        MediaType.entries.associateWith { CompareMediaUiState() }
)

data class CompareMediaUiState(
    val userRates: Map<ComparisonType, List<MediaComparison>> = emptyMap(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)