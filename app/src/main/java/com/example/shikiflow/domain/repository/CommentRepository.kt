package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.sort.ThreadType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.thread.ThreadShort
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    fun getThreadComments(
        topicId: Int,
        page: Int,
        limit: Int = 15
    ): Flow<PagedResult<Comment>>

    suspend fun getComments(
        topicId: Int,
        page: Int = 1,
        limit: Int = 30,
    ): Result<List<Comment>>

    suspend fun getCommentById(commentId: Int): Comment

    fun getThread(threadId: Int): Flow<DataResult<Thread>>

    fun getPaginatedThreads(
        mediaId: Int,
        threadSort: Sort<ThreadType>
    ): Flow<PagingData<ThreadShort>>

    suspend fun publishComment(
        id: Int?,
        topicId: Int,
        parentCommentId: Int?,
        commentBody: String,
        isOfftopic: Boolean
    ): DataResult<Comment>

    suspend fun deleteComment(commentId: Int): DataResult<Boolean>
}