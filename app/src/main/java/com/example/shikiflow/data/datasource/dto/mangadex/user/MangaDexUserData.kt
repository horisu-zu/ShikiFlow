package com.example.shikiflow.data.datasource.dto.mangadex.user

import kotlinx.serialization.Serializable

@Serializable
data class MangaDexUserData(
    val id: String,
    val attributes: MangaDexUserAttributes
)
