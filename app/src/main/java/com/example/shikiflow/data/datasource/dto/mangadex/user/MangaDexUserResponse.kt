package com.example.shikiflow.data.datasource.dto.mangadex.user

import kotlinx.serialization.Serializable

@Serializable
data class MangaDexUserResponse(
    val response: String,
    val result: String,
    val data: MangaDexUserData
)
