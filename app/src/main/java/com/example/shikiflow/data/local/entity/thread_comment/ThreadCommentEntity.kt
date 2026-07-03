package com.example.shikiflow.data.local.entity.thread_comment

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity(tableName = "thread_comment")
data class ThreadCommentEntity(
    @PrimaryKey val id: Int,
    val threadId: Int,
    val senderId: Int,
    val parentId: Int?,
    val commentBody: String,
    val dateTime: Instant,
    val likesCount: Int,
    val isLiked: Boolean
)
