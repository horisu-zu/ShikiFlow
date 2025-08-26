package com.example.shikiflow.domain.model.anime

import kotlinx.serialization.Serializable

@Serializable
data class ShortAnimeRate(
    val status: String,
    val anime: ShortAnime
)
