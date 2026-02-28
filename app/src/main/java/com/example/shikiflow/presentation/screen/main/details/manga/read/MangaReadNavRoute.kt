package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface MangaReadNavRoute : NavKey {
    @Serializable
    data class MangaSelectionScreen(
        val mangaDexIds: List<String>,
        val title: String
    ) : MangaReadNavRoute

    @Serializable
    data class ChaptersScreen(
        val mangaDexId: String,
        val title: String
    ) : MangaReadNavRoute

    @Serializable
    data class ChapterTranslationsScreen(
        val chapterTranslationIds: List<String>,
        val title: String,
        val chapterNumber: String
    ) : MangaReadNavRoute

    @Serializable
    data class ChapterScreen(
        val chapterUiData: ChapterUiData
    ) : MangaReadNavRoute
}

@Serializable
data class ChapterUiData(
    val title: String?,
    val mangaId: String,
    val chapterId: String,
    val scanlationGroupIds: List<String>,
    val uploader: String?,
    val chapterNumber: String?
)