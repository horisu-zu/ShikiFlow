package com.example.shikiflow.domain.model.user.activity

import com.example.shikiflow.domain.model.user.User
import kotlin.time.Instant

data class ActivityReply(
    val id: Int,
    val body: String,
    val markdownBody: String,
    val createdAt: Instant,
    val sender: User,
    val likeCount: Int,
    val isLiked: Boolean
)
