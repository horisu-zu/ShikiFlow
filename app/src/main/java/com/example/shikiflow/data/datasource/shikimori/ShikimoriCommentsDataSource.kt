package com.example.shikiflow.data.datasource.shikimori

import com.example.shikiflow.data.datasource.CommentsDataSource
import com.example.shikiflow.data.mapper.shikimori.ShikimoriCommentsMapper.toDomain
import com.example.shikiflow.data.remote.CommentApi
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.thread.ThreadSort
import javax.inject.Inject

class ShikimoriCommentsDataSource @Inject constructor(
    private val commentApi: CommentApi
): CommentsDataSource {
    override suspend fun getComments(
        topicId: Int,
        page: Int,
        limit: Int
    ): Result<List<Comment>> {
        return try {
            val response = commentApi.getComments(
                commentableId = topicId, 
                page = page,
                limit = limit
            ).map {
                it.toDomain()
            }

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCommentById(commentId: Int): Comment {
        return commentApi.getCommentById(commentId.toString()).toDomain()
    }

    override suspend fun getMediaThreads(
        mediaId: Int,
        page: Int,
        limit: Int,
        threadSort: ThreadSort
    ): Result<List<Thread>> {
        TODO("Not yet implemented")
    }
}