package com.example.shikiflow.domain.model.tracks

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRateRequest(
    @SerialName("user_id") val userId: Long,
    @SerialName("target_id") val targetId: Long,
    val status: String? = null,
    @SerialName("target_type") val targetType: TargetType
)
