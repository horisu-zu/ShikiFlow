package com.example.shikiflow.domain.model.mangadex.chapter_metadata

import com.example.shikiflow.domain.model.mangadex.manga.MangaRelationship

data class MangaChapterMetadata(
    val id: String,
    val type: String,
    val attributes: MetadataAttributes,
    val relationships: List<MangaRelationship>
)
