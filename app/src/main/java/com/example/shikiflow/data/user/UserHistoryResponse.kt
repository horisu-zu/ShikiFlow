package com.example.shikiflow.data.user

import kotlinx.serialization.Serializable

@Serializable
data class UserHistoryResponse(
    val id: Long,
    val created_at: String,
    val description: String,
    val target: Target?
)
