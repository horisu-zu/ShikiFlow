package com.example.shikiflow.data.datasource.dto.mangadex.aggregate

import kotlinx.serialization.Serializable

@Serializable
data class AggregateVolumeResponse(
    val volume: String,
    val count: Int,
    val chapters: Map<String, AggregateChapterResponse>
)
