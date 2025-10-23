package com.example.shikiflow.domain.model.manga

import kotlinx.serialization.Serializable

@Serializable
data class ShortMangaRate(
    val score: Int,
    val status: String,
    val manga: ShortManga
)
