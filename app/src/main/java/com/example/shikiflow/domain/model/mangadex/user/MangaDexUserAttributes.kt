package com.example.shikiflow.domain.model.mangadex.user

import kotlinx.serialization.Serializable

@Serializable
data class MangaDexUserAttributes(
    val username: String,
    val roles: List<String>,
    val version: Int
)
