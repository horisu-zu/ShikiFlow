package com.example.shikiflow.domain.model.mangadex.manga

import kotlinx.serialization.Serializable

@Serializable
data class Relationship(
    val id: String,
    val type: String
)