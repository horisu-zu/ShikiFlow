package com.example.shikiflow.domain.model.tracks

import com.example.graphql.type.UserRateStatusEnum
import kotlinx.serialization.Serializable

@Serializable
data class UserRateRequest(
    val chapters: Int? = null,
    val episodes: Int? = null,
    val rewatches: Int? = null,
    val score: Int? = null,
    val status: UserRateStatusEnum? = null,
    val text: String? = null,
    val volumes: Int? = null
)