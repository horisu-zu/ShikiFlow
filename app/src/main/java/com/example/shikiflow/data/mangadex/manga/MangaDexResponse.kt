package com.example.shikiflow.data.mangadex.manga

import kotlinx.serialization.Serializable

@Serializable
data class MangaDexResponse(
    val data: List<Data>,
    val limit: Int,
    val offset: Int,
    val response: String,
    val result: String,
    val total: Int
)