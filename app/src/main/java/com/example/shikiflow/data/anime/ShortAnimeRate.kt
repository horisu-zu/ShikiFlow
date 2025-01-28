package com.example.shikiflow.data.anime

import kotlinx.serialization.Serializable

@Serializable
data class ShortAnimeRate(
    val status: String,
    val anime: ShortAnime
)
