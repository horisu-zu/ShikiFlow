package com.example.shikiflow.domain.model.thread

import com.example.shikiflow.domain.model.user.UserShort
import kotlin.time.Instant

data class Thread(
    val id: Int,
    val title: String,
    val body: String,
    val categories: List<String>,
    val viewCount: Int,
    val likeCount: Int,
    val replyCount: Int,
    val isLiked: Boolean,
    val createdBy: UserShort?,
    val createdAt: Instant
)
