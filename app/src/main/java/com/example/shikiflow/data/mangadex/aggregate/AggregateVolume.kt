package com.example.shikiflow.data.mangadex.aggregate

import kotlinx.serialization.Serializable

@Serializable
data class AggregateVolume(
    val volume: String,
    val count: Int,
    val chapters: Map<String, AggregateChapter>
)
