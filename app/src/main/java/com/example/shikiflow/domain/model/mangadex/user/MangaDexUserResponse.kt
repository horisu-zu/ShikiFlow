package com.example.shikiflow.domain.model.mangadex.user

import kotlinx.serialization.Serializable

@Serializable
data class MangaDexUserResponse(
    val response: String,
    val result: String,
    val data: MangaDexUserData
)
