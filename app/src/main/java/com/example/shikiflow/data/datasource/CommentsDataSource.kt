package com.example.shikiflow.data.datasource

import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.thread.ThreadSort

interface CommentsDataSource {
    suspend fun getComments(
        topicId: Int,
        page: Int,
        limit: Int
    ): Result<List<Comment>>

    suspend fun getCommentById(commentId: Int): Comment

    suspend fun getMediaThreads(
        mediaId: Int,
        page: Int,
        limit: Int,
        threadSort: ThreadSort
    ): Result<List<Thread>>
}