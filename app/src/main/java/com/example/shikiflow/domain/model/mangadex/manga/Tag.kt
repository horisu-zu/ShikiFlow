package com.example.shikiflow.domain.model.mangadex.manga

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val attributes: AttributesX,
    val id: String,
    val type: String
)