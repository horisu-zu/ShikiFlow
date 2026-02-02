package com.example.shikiflow.data.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShikiCreateRateRequest(
    @SerialName("user_id") val userId: Long,
    @SerialName("target_id") val targetId: Long,
    val status: String? = null,
    @SerialName("target_type") val targetType: String,
    val episodes: Int? = null,
    val chapters: Int? = null,
    val volumes: Int? = null,
    val score: Int? = null,
    val rewatches: Int? = null
)
