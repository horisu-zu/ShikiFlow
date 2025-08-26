package com.example.shikiflow.domain.model.mangadex.manga

import kotlinx.serialization.Serializable

@Serializable
data class MangaData(
    val data: Data,
    val coverUrl: String
)
