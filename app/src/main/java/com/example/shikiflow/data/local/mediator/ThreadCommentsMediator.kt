package com.example.shikiflow.data.local.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.apollographql.apollo.exception.ApolloHttpException
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.local.entity.keys.RemoteKey
import com.example.shikiflow.data.local.entity.thread_comment.CommentEntity
import com.example.shikiflow.data.mapper.local.ThreadCommentMapper.allSenders
import com.example.shikiflow.data.mapper.local.ThreadCommentMapper.toEntityList
import com.example.shikiflow.domain.model.comment.ALComment
import com.example.shikiflow.domain.model.comment.Comment
import org.json.JSONObject

@OptIn(ExperimentalPagingApi::class)
class ThreadCommentsMediator(
    private val appRoomDatabase: AppRoomDatabase,
    private val method: suspend (Int, Int) -> Result<List<Comment>>,
    private val topicId: Int
): RemoteMediator<Int, CommentEntity>() {

    private val threadCommentsDao = appRoomDatabase.threadCommentsDao()
    private val remoteKeysDao = appRoomDatabase.remoteKeysDao()

    private val queryKey = topicId.toString()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CommentEntity>
    ): MediatorResult {
        val page = when(loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKey = remoteKeysDao.getKey(queryKey)

                remoteKey?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        val response = method(page, state.config.pageSize)

        return response.fold(
            onSuccess = { data ->
                val endOfPaginationReached = data.size < state.config.pageSize

                val comments = data.mapNotNull { comment ->
                    if (comment is ALComment) {
                        comment.toEntityList(topicId)
                    } else null
                }.flatten()

                val users = data.mapNotNull { comment ->
                    if (comment is ALComment) {
                        comment.allSenders()
                    } else null
                }.flatten()

                appRoomDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        remoteKeysDao.delete(queryKey)
                        threadCommentsDao.deleteCommentsByThreadId(topicId)
                    }

                    remoteKeysDao.insert(
                        RemoteKey(
                            key = queryKey,
                            prevKey = page - 1,
                            nextKey = if (endOfPaginationReached) null else page + 1
                        )
                    )

                    threadCommentsDao.insertAllComments(comments)
                    threadCommentsDao.insertAllUsers(users)
                }

                MediatorResult.Success(endOfPaginationReached)
            },
            onFailure = { throwable ->
                val error = when (throwable) {
                    is ApolloHttpException -> {
                        val body = throwable.body?.readUtf8().orEmpty()

                        val errorMessage = runCatching {
                            JSONObject(body)
                                .getJSONArray("errors")
                                .getJSONObject(0)
                                .getString("message")
                        }.getOrElse { throwable.message ?: "Unknown Error" }

                        Exception(errorMessage)
                    }
                    else -> {
                        Log.e("MediaTracksMediator", "Exception: $throwable")
                        throwable as? Exception ?: Exception(throwable.message)
                    }
                }
                MediatorResult.Error(error)
            }
        )
    }
}