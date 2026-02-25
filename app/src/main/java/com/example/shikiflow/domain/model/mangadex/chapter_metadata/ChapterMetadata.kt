package com.example.shikiflow.domain.model.mangadex.chapter_metadata

import com.example.shikiflow.domain.model.mangadex.scanlation_group.ScanlationGroup
import com.example.shikiflow.domain.model.mangadex.user.MangaDexUser
import kotlin.time.Instant

data class ChapterMetadata(
    val mangaId: String,
    val chapterId: String,
    val title: String?,
    val chapterNumber: String?,
    val translatedLanguage: String,
    val externalUrl: String?,
    val publishedAt: Instant,
    val scanlationGroups: List<ScanlationGroup> = emptyList(),
    val uploader: MangaDexUser? = null
) {
    companion object {
        fun MangaChapterMetadata.toDomain(
            scanlationGroups: List<ScanlationGroup> = emptyList(),
            uploader: MangaDexUser? = null
        ): ChapterMetadata {
            return ChapterMetadata(
                mangaId = this.relationships.find { it.type == "manga" }?.id ?: "",
                chapterId = this.id,
                title = this.attributes.title,
                chapterNumber = this.attributes.chapter,
                translatedLanguage = this.attributes.translatedLanguage,
                externalUrl = this.attributes.externalUrl,
                publishedAt = this.attributes.publishAt,
                scanlationGroups = scanlationGroups,
                uploader = uploader
            )
        }
    }
}
