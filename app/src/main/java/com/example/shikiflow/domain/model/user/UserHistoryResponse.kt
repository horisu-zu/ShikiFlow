package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.tracks.Target
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserHistoryResponse(
    val id: Long,
    @SerialName("created_at") val createdAt: String,
    val description: String,
    val target: Target?
)
