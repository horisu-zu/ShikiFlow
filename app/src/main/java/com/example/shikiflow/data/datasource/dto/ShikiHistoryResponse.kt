package com.example.shikiflow.data.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShikiHistoryResponse(
    val id: Long,
    @SerialName("created_at") val createdAt: String,
    val description: String,
    val target: Target?
)

@Serializable
data class Target(
    val id: Long,
    val name: String,
    val russian: String,
    val image: ShikiImage,
    val url: String,
    val kind: String?,
    val score: String,
    val status: String,
    val episodes: Int = 0,
    @SerialName("episodes_aired") val episodesAired: Int = 0,
    @SerialName("aired_on") val airedOn: String?,
    @SerialName("released_on") val releasedOn: String?
)