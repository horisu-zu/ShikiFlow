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
import com.example.shikiflow.data.mapper.local.ThreadCommentMapper.toEntity
import com.example.shikiflow.domain.model.comment.ALComment
import com.example.shikiflow.domain.model.user.social.ThreadComment
import org.json.JSONObject

@OptIn(ExperimentalPagingApi::class)
class UserCommentsMediator(
    private val appRoomDatabase: AppRoomDatabase,
    private val method: suspend (Int, Int) -> Result<List<ThreadComment>>,
    private val senderId: Int
): RemoteMediator<Int, CommentEntity>() {

    private val threadCommentsDao = appRoomDatabase.threadCommentsDao()
    private val remoteKeysDao = appRoomDatabase.remoteKeysDao()

    private val queryKey = "sender_$senderId"

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

                val comments = data.mapNotNull { userSocial ->
                    if (userSocial.comment is ALComment) {
                        userSocial.comment.toEntity(userSocial.thread.id)
                    } else null
                }

                val threads = data.map { userSocial ->
                    userSocial.thread.toEntity()
                }

                val user = data.first().comment.sender?.toEntity()

                appRoomDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        remoteKeysDao.delete(queryKey)
                        threadCommentsDao.deleteCommentsBySender(senderId)
                    }

                    remoteKeysDao.insert(
                        RemoteKey(
                            key = queryKey,
                            prevKey = page - 1,
                            nextKey = if (endOfPaginationReached) null else page + 1
                        )
                    )

                    threadCommentsDao.insertAllComments(comments)
                    threadCommentsDao.insertAllThreads(threads)
                    user?.let { threadCommentsDao.insertUser(user) }
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