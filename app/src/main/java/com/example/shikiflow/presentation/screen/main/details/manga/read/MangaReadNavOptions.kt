package com.example.shikiflow.presentation.screen.main.details.manga.read

import com.example.shikiflow.presentation.screen.NavOptions

interface MangaReadNavOptions : NavOptions {
    fun navigateToChapters(mangaDexId: String, title: String)
    fun navigateToChapterTranslations(chapterTranslationIds: List<String>, chapterNumber: String)
    fun navigateToChapter(chapterUiData: ChapterUiData)
}