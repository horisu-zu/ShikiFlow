package com.example.shikiflow.data.manga

import kotlinx.serialization.Serializable

@Serializable
data class ShortMangaRate(
    val status: String,
    val manga: ShortManga
)
