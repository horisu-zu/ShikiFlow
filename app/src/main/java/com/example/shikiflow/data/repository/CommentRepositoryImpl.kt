package com.example.shikiflow.data.repository

import com.example.shikiflow.data.remote.CommentApi
import com.example.shikiflow.domain.model.comment.CommentItem
import com.example.shikiflow.domain.repository.CommentRepository
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentApi: CommentApi
): CommentRepository {

    override suspend fun getComments(
        topicId: String,
        page: Int,
        limit: Int,
    ): List<CommentItem> = commentApi.getComments(
        commentableId = topicId.toInt(),
        page = page,
        limit = limit
    )

    override suspend fun getCommentById(commentId: String): CommentItem
            = commentApi.getCommentById(commentId)
}