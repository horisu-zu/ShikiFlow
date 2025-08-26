package com.example.shikiflow.domain.model.mangadex.chapter_metadata

import com.example.shikiflow.domain.model.mangadex.manga.Relationship
import kotlinx.serialization.Serializable

@Serializable
data class MangaDexChapterMetadata(
    val id: String,
    val type: String,
    val attributes: ChapterMetadataAttributes,
    val relationships: List<Relationship>
)
