package com.example.shikiflow.domain.model.mangadex.aggregate

data class AggregatedChapter(
    val chapter: String,
    val id: String,
    val others: List<String>,
    val count: Int
)
