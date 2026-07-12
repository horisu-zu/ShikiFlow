package com.example.shikiflow.presentation.viewmodel.user.compare

import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.media_details.Genre
import com.example.shikiflow.domain.model.media_details.MediaTagEnum
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.ComparisonType
import com.example.shikiflow.domain.model.user.MediaComparison
import com.example.shikiflow.domain.model.user.User

data class CompareScreenUiState(
    val currentUser: User? = null,
    val targetUserId: Int? = null,
    val mediaType: MediaType? = null,
    val mediaUiState: Map<MediaType, CompareMediaUiState> =
        MediaType.entries.associateWith { CompareMediaUiState() },
    val filters: CompareMediaFilters = CompareMediaFilters(),
    val scoreFormat: ScoreFormat? = null
)

data class CompareMediaUiState(
    val userRates: Map<ComparisonType, List<MediaComparison>> = emptyMap(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)

data class CompareMediaFilters(
    val query: String = "",
    val selectedGenres: List<Genre> = emptyList(),
    val selectedTags: List<MediaTagEnum> = emptyList(),
    val currentUserScoreRange: ClosedFloatingPointRange<Float>? = null,
    val targetUserScoreRange: ClosedFloatingPointRange<Float>? = null
)