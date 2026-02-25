package com.example.shikiflow.data.datasource.dto.mangadex.chapter_metadata

import kotlinx.serialization.Serializable

@Serializable
data class ChapterMetadataResponse(
    val result: String,
    val response: String,
    val data: MangaDexChapterMetadata
)
