package com.example.shikiflow.domain.model.mangadex.aggregate

data class AggregatedVolume(
    val volume: String,
    val count: Int,
    val chapters: List<AggregatedChapter>
)
