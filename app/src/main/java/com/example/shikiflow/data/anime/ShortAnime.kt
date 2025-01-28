package com.example.shikiflow.data.anime

import kotlinx.serialization.Serializable

@Serializable
data class ShortAnime(
    val id: Long,
    val name: String,
    val kind: String
)
