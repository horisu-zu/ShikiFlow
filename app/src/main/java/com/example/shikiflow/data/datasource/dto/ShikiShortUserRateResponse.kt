package com.example.shikiflow.data.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShikiShortUserRateResponse(
    val id: Long,
    val status: String,
    val score: Int,
    @SerialName("target_type") val shikiTargetType: ShikiTargetType
)
