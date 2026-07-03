package com.example.shikiflow.data.datasource

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.sort.ThreadType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow

interface CommentsDataSource {
    fun getPaginatedComments(topicId: Int): Flow<PagingData<Comment>>

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
        threadSort: Sort<ThreadType>
    ): Result<List<Thread>>

    suspend fun toggleCommentLike(commentId: Int): DataResult<Comment>
}