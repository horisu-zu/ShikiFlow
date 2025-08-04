package com.example.shikiflow.data.mangadex.aggregate

import kotlinx.serialization.Serializable

@Serializable
data class AggregateResponse(
    val result: String,
    val volumes: Map<String, AggregateVolume>
)
