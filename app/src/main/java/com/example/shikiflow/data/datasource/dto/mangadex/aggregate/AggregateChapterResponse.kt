package com.example.shikiflow.data.datasource.dto.mangadex.aggregate

import kotlinx.serialization.Serializable

@Serializable
data class AggregateChapterResponse(
    val chapter: String,
    val id: String,
    val others: List<String>,
    val count: Int
)
