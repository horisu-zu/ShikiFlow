package com.example.shikiflow.data.datasource.dto.mangadex

import com.example.shikiflow.data.datasource.dto.mangadex.chapter_metadata.MangaDexChapterMetadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChaptersListResponse(
    val result: String,
    val response: String?,
    val errors: List<String>?,
    @SerialName("data") val chaptersData: List<MangaDexChapterMetadata>
)
