package com.example.shikiflow.data.datasource.dto.mangadex.user

import kotlinx.serialization.Serializable

@Serializable
data class MangaDexUserAttributes(
    val username: String,
    val roles: List<String>,
    val version: Int
)
