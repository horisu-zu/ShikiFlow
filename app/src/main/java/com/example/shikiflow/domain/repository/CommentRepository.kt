package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.thread.ThreadSort
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    suspend fun getComments(
        topicId: Int,
        page: Int = 1,
        limit: Int = 30,
    ): Result<List<Comment>>

    suspend fun getCommentById(commentId: Int): Comment

    fun getPaginatedComments(
        topicId: Int
    ): Flow<PagingData<Comment>>

    fun getPaginatedThreads(
        mediaId: Int,
        threadSort: ThreadSort
    ): Flow<PagingData<Thread>>
}