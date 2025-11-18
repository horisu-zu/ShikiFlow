package com.example.shikiflow.domain.model.tracks

import com.example.graphql.type.UserRateStatusEnum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRate(
    val id: Long,
    val status: UserRateStatusEnum,
    val score: Int,
    @SerialName("target_type") val targetType: TargetType
)