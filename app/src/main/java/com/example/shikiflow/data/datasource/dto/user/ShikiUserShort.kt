package com.example.shikiflow.data.datasource.dto.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShikiUserShort(
    val id: Long,
    val nickname: String,
    @SerialName("in_friends") val isFollowing: Boolean
)
