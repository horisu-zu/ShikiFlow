package com.example.shikiflow.data.datasource.anilist

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.apollographql.apollo.ApolloClient
import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.fetchPolicy
import com.example.graphql.anilist.MediaThreadsQuery
import com.example.graphql.anilist.ToggleLikeMutation
import com.example.graphql.anilist.TopicCommentQuery
import com.example.graphql.anilist.TopicCommentsQuery
import com.example.graphql.anilist.type.LikeableType
import com.example.shikiflow.data.datasource.CommentsDataSource
import com.example.shikiflow.data.local.AppRoomDatabase
import com.example.shikiflow.data.local.mediator.ThreadCommentsMediator
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.findComment
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.toAnilistThreadSort
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.toDomain
import com.example.shikiflow.data.mapper.local.ThreadCommentMapper.toTree
import com.example.shikiflow.di.annotations.AnilistApollo
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.sort.ThreadType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class AnilistThreadsDataSource @Inject constructor(
    @param:AnilistApollo private val apolloClient: ApolloClient,
    private val appRoomDatabase: AppRoomDatabase
): CommentsDataSource, BaseNetworkRepository() {

    override fun getPaginatedComments(topicId: Int): Flow<PagingData<Comment>> {
        val threadCommentsDao = appRoomDatabase.threadCommentsDao()

        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = true,
                prefetchDistance = 5,
                initialLoadSize = 15
            ),
            remoteMediator = ThreadCommentsMediator(
                appRoomDatabase = appRoomDatabase,
                method = { page, limit ->
                    getComments(topicId, page, limit)
                },
                topicId = topicId
            ),
            pagingSourceFactory = { threadCommentsDao.getCommentsByThreadId(topicId) }
        ).flow.map { pagingData ->
            pagingData.map { rootComment ->
                val subtree = threadCommentsDao.getSubtree(rootComment.comment.id)

                subtree.toTree(rootId = rootComment.comment.id)
            }
        }
    }

    override suspend fun getComments(
        topicId: Int,
        page: Int,
        limit: Int
    ): Result<List<Comment>> {
        val topicCommentsQuery = TopicCommentsQuery(
            threadId = topicId,
            page = page,
            perPage = limit
        )

        val commentsResponse = apolloClient.query(topicCommentsQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return commentsResponse.toResult().map { data ->
            data.Page
                ?.threadComments
                ?.mapNotNull { it?.toDomain() }
                ?: emptyList()
        }
    }

    override suspend fun getCommentById(commentId: Int): Comment {
        val response = apolloClient
            .query(TopicCommentQuery(commentId))
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return response.data?.let { responseData ->
            val data = responseData
                .ThreadComment
                .orEmpty()
                .map { it?.toDomain() }

            data.firstNotNullOfOrNull {
                it?.findComment(commentId)
            } ?: throw NoSuchElementException("No Comment with ID: $commentId")
        } ?: throw Exception(response.exception)
    }

    override suspend fun getMediaThreads(
        mediaId: Int,
        page: Int,
        limit: Int,
        threadSort: Sort<ThreadType>
    ): Result<List<Thread>> {
        val threadsQuery = MediaThreadsQuery(
            mediaId = mediaId,
            page = page,
            perPage = limit,
            sort = threadSort.toAnilistThreadSort()
        )

        val response = apolloClient.query(threadsQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return response.toResult().map { data ->
            data.Page
                ?.threads
                ?.mapNotNull { it?.aLThread?.toDomain() }
                ?: emptyList()
        }
    }

    override suspend fun toggleCommentLike(commentId: Int): DataResult<Comment> {
        val likeMutation = ToggleLikeMutation(
            likeableId = commentId,
            type = LikeableType.THREAD_COMMENT
        )

        val response = apolloClient.mutation(likeMutation)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return response.asDataResult { data ->
            data.ToggleLikeV2?.onThreadComment?.aLThreadComment?.toDomain()
                ?: throw NoSuchElementException("No Comment with ID: $commentId")
        }
    }
}