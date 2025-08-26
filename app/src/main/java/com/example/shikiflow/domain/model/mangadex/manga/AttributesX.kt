package com.example.shikiflow.domain.model.mangadex.manga

import kotlinx.serialization.Serializable

@Serializable
data class AttributesX(
    val group: String,
    val name: Name,
    val version: Int
)