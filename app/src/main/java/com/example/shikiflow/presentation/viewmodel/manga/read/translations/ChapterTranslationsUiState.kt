package com.example.shikiflow.presentation.viewmodel.manga.read.translations

import com.example.shikiflow.domain.model.mangadex.chapter_metadata.ChapterMetadata
import com.example.shikiflow.presentation.UiState

data class ChapterTranslationsUiState(
    val chapterIds: List<String> = emptyList(),
    val chapterTranslations: List<ChapterMetadata> = emptyList(),

    override val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    override val errorMessage: String? = null
): UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}