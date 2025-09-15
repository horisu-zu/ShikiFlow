package com.example.shikiflow.domain.model.comment

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import kotlin.time.Instant


@Serializable
data class User(
    val avatar: String,
    val id: Int,
    val image: Image,
    @Contextual @SerialName("last_online_at") val lastOnlineAt: Instant,
    val nickname: String,
    val url: String
)