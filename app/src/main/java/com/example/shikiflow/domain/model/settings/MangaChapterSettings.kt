package com.example.shikiflow.domain.model.settings

import com.example.shikiflow.presentation.screen.main.details.manga.read.ChapterUIMode

data class MangaChapterSettings(
    val chapterUIMode: ChapterUIMode = ChapterUIMode.SCROLL,
    val isDataSaverEnabled: Boolean = false
)
