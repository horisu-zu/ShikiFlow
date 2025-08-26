package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.tracks.Target
import kotlinx.serialization.Serializable

@Serializable
data class UserHistoryResponse(
    val id: Long,
    val created_at: String,
    val description: String,
    val target: Target?
)
