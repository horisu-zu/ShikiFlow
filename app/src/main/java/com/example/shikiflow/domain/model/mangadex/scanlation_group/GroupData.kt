package com.example.shikiflow.domain.model.mangadex.scanlation_group

import com.example.shikiflow.domain.model.mangadex.manga.Relationship
import kotlinx.serialization.Serializable

@Serializable
data class GroupData(
    val attributes: ScanlationGroupAttributes,
    val id: String,
    val relationships: List<Relationship>,
    val type: String
)