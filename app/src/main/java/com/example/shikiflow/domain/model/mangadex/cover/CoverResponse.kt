package com.example.shikiflow.domain.model.mangadex.cover

import kotlinx.serialization.Serializable

@Serializable
data class CoverResponse(
    val result: String,
    val response: String,
    val data: CoverData
)
