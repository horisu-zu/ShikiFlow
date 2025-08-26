package com.example.shikiflow.domain.model.mangadex.chapter

import kotlinx.serialization.Serializable

@Serializable
data class ChapterResponse(
    val result: String,
    val baseUrl: String,
    val chapter: MangaDexChapter
)
