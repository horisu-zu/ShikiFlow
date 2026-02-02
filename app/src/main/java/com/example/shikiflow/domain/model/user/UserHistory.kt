package com.example.shikiflow.domain.model.user

import kotlin.time.Instant

data class UserHistory(
    val id: Int,
    val mediaId: Int,
    val title: String,
    val coverImage: String,
    val description: String,
    val createdAt: Instant
)