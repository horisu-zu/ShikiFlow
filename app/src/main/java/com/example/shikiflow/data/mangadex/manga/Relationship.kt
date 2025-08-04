package com.example.shikiflow.data.mangadex.manga

import kotlinx.serialization.Serializable

@Serializable
data class Relationship(
    val id: String,
    val type: String
)