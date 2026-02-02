package com.example.shikiflow.data.datasource.dto.comment

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class ShikiUser(
    val avatar: String,
    val id: Int,
    @SerialName("image") val shikiCommentImage: ShikiCommentImage,
    @Contextual @SerialName("last_online_at") val lastOnlineAt: Instant,
    val nickname: String,
    val url: String
)