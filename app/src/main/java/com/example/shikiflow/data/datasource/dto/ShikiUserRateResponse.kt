package com.example.shikiflow.data.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShikiUserRateResponse(
    val id: Long,
    @SerialName("user_id") val userId: Long,
    @SerialName("target_id") val targetId: Long,
    @SerialName("target_type") val targetType: String,
    val score: Int,
    val status: String,
    val rewatches: Int,
    val episodes: Int? = null,
    val volumes: Int? = null,
    val chapters: Int? = null,
    val text: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)