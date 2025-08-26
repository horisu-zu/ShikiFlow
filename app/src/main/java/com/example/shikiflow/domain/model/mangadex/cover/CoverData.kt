package com.example.shikiflow.domain.model.mangadex.cover

import com.example.shikiflow.domain.model.mangadex.manga.Relationship
import kotlinx.serialization.Serializable

@Serializable
data class CoverData(
    val attributes: CoverAttributes,
    val id: String,
    val relationships: List<Relationship>,
    val type: String
)