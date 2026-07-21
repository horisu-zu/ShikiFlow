package com.example.shikiflow.data.datasource

import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.comment.CommentableType
import com.example.shikiflow.domain.model.sort.ThreadType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.thread.Like
import com.example.shikiflow.domain.model.thread.LikeableType
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.thread.ThreadShort
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import kotlinx.coroutines.flow.Flow

interface CommentsDataSource {
    fun getThreadComments(
        topicId: Int,
        page: Int,
        limit: Int
    ): Flow<PagedResult<Comment>>

    suspend fun getComments(
        topicId: Int,
        page: Int,
        limit: Int
    ): Result<List<Comment>>

    suspend fun getCommentById(commentId: Int): Comment

    fun getThread(threadId: Int): Flow<DataResult<Thread>>

    suspend fun getMediaThreads(
        mediaId: Int,
        page: Int,
        limit: Int,
        threadSort: Sort<ThreadType>
    ): Result<List<ThreadShort>>

    suspend fun toggleLike(
        id: Int,
        likeableType: LikeableType
    ): DataResult<Like>

    suspend fun publishComment(
        id: Int?,
        topicId: Int,
        commentableType: CommentableType,
        parentCommentId: Int?,
        commentBody: String,
        isOfftopic: Boolean
    ): DataResult<Comment>

    suspend fun deleteComment(
        id: Int
    ): DataResult<Boolean>
}