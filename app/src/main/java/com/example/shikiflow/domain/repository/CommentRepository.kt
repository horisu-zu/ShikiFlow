package com.example.shikiflow.domain.repository

import com.example.shikiflow.data.common.comment.CommentItem
import com.example.shikiflow.data.api.CommentApi
import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val commentApi: CommentApi
) {

    suspend fun getComments(
        topicId: String,
        page: Int = 1,
        limit: Int = 30,
    ): List<CommentItem> = commentApi.getComments(
        commentableId = topicId.toInt(),
        page = page,
        limit = limit
    )
}