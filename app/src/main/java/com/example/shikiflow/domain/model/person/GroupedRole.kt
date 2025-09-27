package com.example.shikiflow.domain.model.person

import kotlinx.serialization.Serializable

@Serializable
data class GroupedRole(
    val role: String,
    val count: Int
)