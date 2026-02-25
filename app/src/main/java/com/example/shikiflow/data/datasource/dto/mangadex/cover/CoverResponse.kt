package com.example.shikiflow.data.datasource.dto.mangadex.cover

import kotlinx.serialization.Serializable

@Serializable
data class CoverResponse(
    val result: String,
    val response: String,
    val data: CoverData
)
