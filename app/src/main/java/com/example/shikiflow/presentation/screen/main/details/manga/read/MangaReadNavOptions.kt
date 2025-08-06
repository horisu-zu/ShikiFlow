package com.example.shikiflow.presentation.screen.main.details.manga.read

import com.example.shikiflow.presentation.screen.MainNavOptions

interface MangaReadNavOptions : MainNavOptions {
    fun navigateToChapterTranslations(chapterTranslationIds: List<String>, chapterNumber: String)
    fun navigateToChapter(mangaDexChapterId: String)
}