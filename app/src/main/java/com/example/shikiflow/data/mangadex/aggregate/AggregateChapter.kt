package com.example.shikiflow.data.mangadex.aggregate

import kotlinx.serialization.Serializable

@Serializable
data class AggregateChapter(
    val chapter: String,
    val id: String,
    val others: List<String>,
    val count: Int
)
