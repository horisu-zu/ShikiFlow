package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.comment.CommentItem

interface CommentRepository {
    suspend fun getComments(
        topicId: String,
        page: Int = 1,
        limit: Int = 30,
    ): List<CommentItem>

    suspend fun getCommentById(commentId: String): CommentItem
}