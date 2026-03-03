package com.example.shikiflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.CommentsDataSource
import com.example.shikiflow.data.local.source.CommentPagingSource
import com.example.shikiflow.data.local.source.ThreadPagingSource
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.thread.ThreadSort
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val shikimoriDataSource: CommentsDataSource,
    private val anilistDataSource: CommentsDataSource,
    private val settingsRepository: SettingsRepository
): CommentRepository {

    private fun getSource() = runBlocking {
        when(settingsRepository.authTypeFlow.first()) {
            AuthType.SHIKIMORI -> shikimoriDataSource
            AuthType.ANILIST -> anilistDataSource
        }
    }

    override suspend fun getComments(
        topicId: Int,
        page: Int,
        limit: Int,
    ): Result<List<Comment>> = getSource().getComments(topicId, page, limit)

    override suspend fun getCommentById(commentId: Int): Comment = getSource().getCommentById(commentId)

    override fun getPaginatedComments(topicId: Int): Flow<PagingData<Comment>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = true,
                prefetchDistance = 5,
                initialLoadSize = 15
            ),
            pagingSourceFactory = {
                CommentPagingSource(
                    commentsRepository = this,
                    topicId = topicId
                )
            }
        ).flow
    }

    override fun getPaginatedThreads(
        mediaId: Int,
        threadSort: ThreadSort
    ): Flow<PagingData<Thread>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = true,
                prefetchDistance = 5,
                initialLoadSize = 15
            ),
            pagingSourceFactory = {
                ThreadPagingSource(
                    commentsDataSource = getSource(),
                    mediaId = mediaId,
                    threadSort = threadSort
                )
            }
        ).flow
    }
}