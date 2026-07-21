package com.example.shikiflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.CommentsDataSource
import com.example.shikiflow.data.local.source.GenericPagingSource
import com.example.shikiflow.di.annotations.AniList
import com.example.shikiflow.di.annotations.Shikimori
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.comment.CommentableType
import com.example.shikiflow.domain.model.sort.ThreadType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.thread.Like
import com.example.shikiflow.domain.model.thread.LikeableType
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.thread.ThreadShort
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(FlowPreview::class)
class CommentRepositoryImpl @Inject constructor(
    @param:Shikimori private val shikimoriDataSource: CommentsDataSource,
    @param:AniList private val anilistDataSource: CommentsDataSource,
    private val settingsRepository: SettingsRepository
): CommentRepository, BaseNetworkRepository() {

    private val dataSource = settingsRepository.authTypeFlow
        .filterNotNull()
        .distinctUntilChanged()
        .map { authType ->
            when(authType) {
                AuthType.SHIKIMORI -> shikimoriDataSource
                AuthType.ANILIST -> anilistDataSource
            }
        }

    override fun getThreadComments(
        topicId: Int,
        page: Int,
        limit: Int
    ): Flow<PagedResult<Comment>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getThreadComments(topicId, page, limit)
        }
    }

    override suspend fun getComments(
        topicId: Int,
        page: Int,
        limit: Int,
    ): Result<List<Comment>> {
        return withSourceSuspend(dataSource) { dataSource ->
            dataSource.getComments(topicId, page, limit)
        }
    }

    override suspend fun getCommentById(commentId: Int): Comment {
        return withSourceSuspend(dataSource) { dataSource ->
            dataSource.getCommentById(commentId)
        }
    }

    override fun getThread(threadId: Int): Flow<DataResult<Thread>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getThread(threadId)
        }
    }

    override fun getPaginatedThreads(
        mediaId: Int,
        threadSort: Sort<ThreadType>
    ): Flow<PagingData<ThreadShort>> {
        return withSource(dataSource) { dataSource ->
            Pager(
                config = PagingConfig(
                    pageSize = 15,
                    enablePlaceholders = true,
                    prefetchDistance = 5,
                    initialLoadSize = 15
                ),
                pagingSourceFactory = {
                    GenericPagingSource(
                        method = { page, limit ->
                            dataSource.getMediaThreads(mediaId, page, limit, threadSort)
                        }
                    )
                }
            ).flow
        }
    }

    override suspend fun toggleLike(
        id: Int,
        likeableType: LikeableType
    ): DataResult<Like> {
        return withSourceSuspend(dataSource) { dataSource ->
            dataSource.toggleLike(id, likeableType)
        }
    }

    override suspend fun publishComment(
        id: Int?,
        topicId: Int,
        commentableType: CommentableType,
        parentCommentId: Int?,
        commentBody: String,
        isOfftopic: Boolean
    ): DataResult<Comment> {
        return withSourceSuspend(dataSource) { dataSource ->
            dataSource.publishComment(
                id = id,
                topicId = topicId,
                commentableType = commentableType,
                parentCommentId = parentCommentId,
                commentBody = commentBody,
                isOfftopic = isOfftopic
            )
        }
    }

    override suspend fun deleteComment(commentId: Int): DataResult<Boolean> {
        return withSourceSuspend(dataSource) { dataSource ->
            dataSource.deleteComment(commentId)
        }
    }
}