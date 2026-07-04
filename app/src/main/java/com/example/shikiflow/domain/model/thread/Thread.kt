package com.example.shikiflow.domain.model.thread

import com.example.shikiflow.domain.model.user.User
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Thread(
    val id: Int,
    val title: String?,
    val body: String?,
    val categories: List<String>,
    val viewCount: Int,
    val replyCount: Int,
    val lastReplyUser: User?,
    val lastRepliedAt: Instant?,
    val createdBy: User?,
    val createdAt: Instant
) {
    companion object {
        fun createEmpty(): Thread {
            return Thread(
                id = 0,
                title = "",
                body = null,
                categories = emptyList(),
                viewCount = 0,
                replyCount = 0,
                lastReplyUser = null,
                lastRepliedAt = null,
                createdBy = null,
                createdAt = Instant.DISTANT_PAST
            )
        }
    }
}