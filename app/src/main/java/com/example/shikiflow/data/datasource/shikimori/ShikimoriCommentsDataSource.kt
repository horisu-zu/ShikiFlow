package com.example.shikiflow.data.datasource.shikimori

import com.example.shikiflow.data.datasource.CommentsDataSource
import com.example.shikiflow.data.datasource.dto.comment.ShikiCreateComment
import com.example.shikiflow.data.datasource.dto.comment.ShikiUpdateComment
import com.example.shikiflow.data.mapper.shikimori.ShikimoriCommentsMapper.toDomain
import com.example.shikiflow.data.mapper.shikimori.ShikimoriCommentsMapper.toShikiType
import com.example.shikiflow.data.remote.CommentApi
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.comment.CommentableType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.ThreadType
import com.example.shikiflow.domain.model.thread.Like
import com.example.shikiflow.domain.model.thread.LikeableType
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.thread.ThreadShort
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ShikimoriCommentsDataSource @Inject constructor(
    private val commentApi: CommentApi
): CommentsDataSource {
    override fun getThreadComments(
        topicId: Int,
        page: Int,
        limit: Int
    ): Flow<PagedResult<Comment>> = flow {
        emit(PagedResult.Loading)

        try {
            val response = commentApi.getComments(
                commentableId = topicId,
                page = page,
                limit = limit
            ).map { comment ->
                comment.toDomain()
            }.take(limit)

            emit(PagedResult.Success(
                list = response,
                currentPage = page,
                hasNextPage = response.size == limit
            ))
        } catch (e: IOException) {
            emit(PagedResult.Error("${e.message}: Missing Internet Connection"))
        } catch (e: HttpException) {
            emit(PagedResult.Error(e.message ?: "Unknown Error"))
        }
    }

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
            }.take(limit) //Shikimori API returns loadSize + 1

            Result.success(response)
        } catch (e: IOException) {
            Result.failure(Exception("${e.message}: Missing Internet Connection"))
        } catch (e: HttpException) {
            Result.failure(e)
        }
    }

    override suspend fun getCommentById(commentId: Int): Comment {
        return commentApi.getCommentById(commentId.toString()).toDomain()
    }

    override fun getThread(threadId: Int): Flow<DataResult<Thread>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMediaThreads(
        mediaId: Int,
        page: Int,
        limit: Int,
        threadSort: Sort<ThreadType>
    ): Result<List<ThreadShort>> {
        TODO("Not yet implemented")
    }

    override suspend fun toggleLike(id: Int, likeableType: LikeableType): DataResult<Like> {
        TODO("Not yet implemented")
    }

    override suspend fun publishComment(
        id: Int?,
        topicId: Int,
        commentableType: CommentableType,
        parentCommentId: Int?,
        commentBody: String,
        isOfftopic: Boolean
    ): DataResult<Comment> {
        return try {
            val response = if (id != null) {
                commentApi.updateComment(
                    commentId = id.toString(),
                    comment = ShikiUpdateComment(commentBody)
                )
            } else {
                commentApi.createComment(
                    comment = ShikiCreateComment(
                        body = commentBody,
                        commentableId = topicId,
                        commentableType = commentableType.toShikiType(),
                        isOfftopic = isOfftopic
                    )
                )
            }

            return DataResult.Success(response.toDomain())
        } catch (e: Exception) {
            DataResult.Error(e.message ?: "Unknown Error")
        }
    }
}