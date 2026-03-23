package com.example.shikiflow.presentation.viewmodel.manga.read.chapter

import com.example.shikiflow.domain.model.settings.MangaChapterSettings

data class ChapterUiState(
    val chapterId: String? = null,
    val chapterData: List<String> = emptyList(),
    val uiSettings: MangaChapterSettings = MangaChapterSettings(),
    val isNavigationVisible: Boolean = false,

    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val chapterError: String? = null
)