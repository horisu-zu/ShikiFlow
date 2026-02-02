package com.example.shikiflow.domain.model.user

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class User(
    val id: String,
    val avatarUrl: String,
    val nickname: String,
    val lastOnlineAt: Instant? = null
)