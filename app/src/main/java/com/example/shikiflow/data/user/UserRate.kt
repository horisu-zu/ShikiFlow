package com.example.shikiflow.data.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRate(
    val id: Long,
    val status: String,
    @SerialName("target_type") val targetType: TargetType
)
