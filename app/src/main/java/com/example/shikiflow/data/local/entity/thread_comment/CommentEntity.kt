package com.example.shikiflow.data.local.entity.thread_comment

import androidx.room.Embedded
import androidx.room.Relation

data class CommentEntity(
    @Embedded val comment: ThreadCommentEntity,
    @Relation(
        parentColumn = "senderId",
        entityColumn = "id"
    )
    val sender: UserShortEntity,
    @Relation(
        parentColumn = "threadId",
        entityColumn = "id"
    )
    val thread: ThreadEntity?
)
