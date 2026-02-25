package com.example.shikiflow.data.datasource.dto.mangadex.manga

import kotlinx.serialization.Serializable

@Serializable
data class Relationship(
    val id: String,
    val type: String
)