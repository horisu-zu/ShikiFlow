package com.example.shikiflow.domain.model.mangadex.user

import kotlinx.serialization.Serializable

@Serializable
data class MangaDexUserData(
    val id: String,
    val attributes: MangaDexUserAttributes
)
