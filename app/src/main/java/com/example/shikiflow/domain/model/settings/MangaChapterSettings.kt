package com.example.shikiflow.domain.model.settings

data class MangaChapterSettings(
    val chapterUIMode: ChapterUIMode = ChapterUIMode.SCROLL,
    val isDataSaverEnabled: Boolean = false
)

enum class ChapterUIMode {
    PAGE,
    SCROLL
}
