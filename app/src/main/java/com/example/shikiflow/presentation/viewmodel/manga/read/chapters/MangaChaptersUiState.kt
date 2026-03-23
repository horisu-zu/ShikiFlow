package com.example.shikiflow.presentation.viewmodel.manga.read.chapters

import com.example.shikiflow.domain.model.sort.SortDirection

data class MangaChaptersUiState(
    val mangaDexId: String? = null,
    val chaptersMap: Map<String, List<String>> = emptyMap(),
    val sortDirection: SortDirection = SortDirection.ASCENDING,

    val errorMessage: String? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false
)