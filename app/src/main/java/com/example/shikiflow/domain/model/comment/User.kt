package com.example.shikiflow.domain.model.comment

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val avatar: String,
    val id: Int,
    val image: Image,
    @SerialName("last_online_at") val lastOnlineAt: Instant,
    val nickname: String,
    val url: String
)