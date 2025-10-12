package com.example.shikiflow.domain.model.mangadex.aggregate

import kotlinx.serialization.Serializable

@Serializable
data class AggregateResponse(
    val result: String,
    @Serializable(with = VolumesSerializer::class)
    val volumes: Map<String, AggregateVolume> //If empty, response returns '[]' instead of '{}'
)
