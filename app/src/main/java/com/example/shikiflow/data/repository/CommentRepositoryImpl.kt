package com.example.shikiflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.CommentsDataSource
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.local.source.GenericPagingSource
import com.example.shikiflow.data.mapper.local.ThreadCommentMapper.toTree
import com.example.shikiflow.di.annotations.AniList
import com.example.shikiflow.di.annotations.Shikimori
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.ALComment
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.sort.ThreadType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    @param:Shikimori private val shikimoriDataSource: CommentsDataSource,
    @param:AniList private val anilistDataSource: CommentsDataSource,
    private val settingsRepository: SettingsRepository,
    private val appRoomDatabase: AppRoomDatabase
): CommentRepository, BaseNetworkRepository() {

    private val dataSource = settingsRepository.authTypeFlow
        .filterNotNull()
        .map { authType ->
            when(authType) {
                AuthType.SHIKIMORI -> shikimoriDataSource
                AuthType.ANILIST -> anilistDataSource
            }
        }
        .distinctUntilChanged()

    private val threadCommentsDao = appRoomDatabase.threadCommentsDao()

    override suspend fun getComments(
        topicId: Int,
        page: Int,
        limit: Int,
    ): Result<List<Comment>> {
        return withSourceSuspend(dataSource) { dataSource ->
            dataSource.getComments(topicId, page, limit)
        }
    }

    override fun observeComments(commentsIds: Set<Int>): Flow<List<Comment>> {
        return threadCommentsDao.getComments(commentsIds).map { commentEntities ->
            commentEntities.map { commentEntity ->
                val subtree = threadCommentsDao.getSubtree(commentEntity.comment.id)

                subtree.toTree(rootId = commentEntity.comment.id)
            }
        }
    }

    override suspend fun getCommentById(commentId: Int): Comment {
        return withSourceSuspend(dataSource) { dataSource ->
            dataSource.getCommentById(commentId)
        }
    }

    override fun getPaginatedComments(topicId: Int): Flow<PagingData<Comment>> {
        return withSource(dataSource) { dataSource ->
            dataSource.getPaginatedComments(topicId)
        }
    }

    override fun getPaginatedThreads(
        mediaId: Int,
        threadSort: Sort<ThreadType>
    ): Flow<PagingData<Thread>> {
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

    override suspend fun toggleCommentLike(commentId: Int) {
        return withSourceSuspend(dataSource) { dataSource ->
            dataSource.toggleCommentLike(commentId).let { result ->
                if (result is DataResult.Success && result.data is ALComment) {
                    val comment = threadCommentsDao.getCommentById(commentId)

                    comment?.let {
                        threadCommentsDao.updateComment(
                            comment = comment.copy(
                                likesCount = result.data.likesCount,
                                isLiked = result.data.isLiked
                            )
                        )
                    }
                }
            }
        }
    }
}