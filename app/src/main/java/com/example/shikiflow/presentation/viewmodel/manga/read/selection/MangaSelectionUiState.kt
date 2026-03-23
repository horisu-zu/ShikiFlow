package com.example.shikiflow.presentation.viewmodel.manga.read.selection

import com.example.shikiflow.domain.model.mangadex.manga.MangaData
import com.example.shikiflow.presentation.UiState

data class MangaSelectionUiState(
    val mangaDexIds: List<String> = emptyList(),
    val mangaList: List<MangaData> = emptyList(),

    override val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    override val errorMessage: String? = null
): UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}