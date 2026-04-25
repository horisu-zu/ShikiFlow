package com.example.shikiflow.presentation.viewmodel.manga.read.chapter

import com.example.shikiflow.domain.model.settings.MangaChapterSettings

data class ChapterUiState(
    val mangaId: String? = null,
    val malId: Int? = null,
    val chapterId: String? = null,
    val chapterNumber: Double? = null,
    val scanlationGroupsIds: List<String>? = null,
    val uploader: String? = null,
    val chapterData: List<String> = emptyList(),

    val currentPageIndex: Int = 0,
    val uiSettings: MangaChapterSettings = MangaChapterSettings(),
    val isNavigationVisible: Boolean = false,

    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val chapterError: String? = null
)