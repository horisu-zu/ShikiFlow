package com.example.shikiflow.data.datasource.dto.mangadex.scanlation_group

import com.example.shikiflow.data.datasource.dto.mangadex.manga.Relationship
import kotlinx.serialization.Serializable

@Serializable
data class GroupDataResponse(
    val attributes: ScanlationGroupAttributes,
    val id: String,
    val relationships: List<Relationship>,
    val type: String
)