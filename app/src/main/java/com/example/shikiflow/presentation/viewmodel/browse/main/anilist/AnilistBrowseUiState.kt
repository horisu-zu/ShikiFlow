package com.example.shikiflow.presentation.viewmodel.browse.main.anilist

import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.browse.BrowseType

data class AnilistBrowseUiState(
    val sections: Map<BrowseType, AnilistBrowseSectionUiState> = emptyMap()
)

data class AnilistBrowseSectionUiState (
    val browseMedia: List<BrowseMedia> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)