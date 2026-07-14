package com.example.shikiflow.domain.model.thread

import com.example.shikiflow.domain.model.user.User
import kotlin.time.Instant

data class ThreadShort(
    val id: Int,
    val title: String,
    val viewCount: Int,
    val replyCount: Int,
    val lastReplyUser: User?,
    val lastRepliedAt: Instant?
)