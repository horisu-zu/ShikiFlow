package com.example.shikiflow.data.datasource.dto.mangadex.aggregate

import kotlinx.serialization.Serializable

@Serializable
data class AggregateResponse(
    val result: String,
    @Serializable(with = VolumesSerializer::class)
    val volumes: Map<String, AggregateVolumeResponse> //If empty, response returns '[]' instead of '{}'
)
