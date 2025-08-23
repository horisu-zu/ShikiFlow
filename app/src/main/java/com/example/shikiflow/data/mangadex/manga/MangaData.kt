package com.example.shikiflow.data.mangadex.manga

import kotlinx.serialization.Serializable

@Serializable
data class MangaData(
    val data: Data,
    val coverUrl: String
)
