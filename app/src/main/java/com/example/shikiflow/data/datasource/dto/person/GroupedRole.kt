package com.example.shikiflow.data.datasource.dto.person

import kotlinx.serialization.Serializable

@Serializable
data class GroupedRole(
    val role: String,
    val count: Int
)