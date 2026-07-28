package com.example.shikiflow.data.datasource.anilist

import androidx.paging.ExperimentalPagingApi
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.fetchPolicy
import com.example.graphql.anilist.DeleteThreadCommentMutation
import com.example.graphql.anilist.MediaThreadQuery
import com.example.graphql.anilist.MediaThreadsQuery
import com.example.graphql.anilist.PublishThreadCommentMutation
import com.example.graphql.anilist.TopicCommentQuery
import com.example.graphql.anilist.TopicCommentsQuery
import com.example.shikiflow.data.datasource.CommentsDataSource
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.findComment
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.toAnilistThreadSort
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.toDomainThread
import com.example.shikiflow.di.annotations.AnilistApollo
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.sort.ThreadType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.thread.ThreadShort
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class AnilistThreadsDataSource @Inject constructor(
    @param:AnilistApollo private val apolloClient: ApolloClient
): CommentsDataSource, BaseNetworkRepository() {
    override fun getThreadComments(
        topicId: Int,
        page: Int,
        limit: Int
    ): Flow<PagedResult<Comment>> {
        val topicCommentsQuery = TopicCommentsQuery(
            threadId = topicId,
            page = page,
            perPage = limit
        )

        val commentsResponse = apolloClient.query(topicCommentsQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .toFlow()

        return commentsResponse.asPagedResult(
            page = { it.Page?.pageInfo?.commonPage }
        ) { data ->
            data.Page
                ?.threadComments
                ?.mapNotNull { it?.toDomain() }
                ?: emptyList()
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

    override fun getThread(threadId: Int): Flow<DataResult<Thread>> {
        val threadQuery = MediaThreadQuery(threadId = threadId)

        val response = apolloClient.query(threadQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .toFlow()

        return response.asDataResult { data ->
            data.Thread?.aLThread?.toDomainThread()
                ?: throw IllegalStateException("No data returned from Thread Query")
        }
    }

    override suspend fun getMediaThreads(
        mediaId: Int,
        page: Int,
        limit: Int,
        threadSort: Sort<ThreadType>
    ): Result<List<ThreadShort>> {
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
                ?.mapNotNull { it?.aLThreadShort?.toDomainThread() }
                ?: emptyList()
        }
    }

    override suspend fun publishComment(
        id: Int?,
        topicId: Int,
        parentCommentId: Int?,
        commentBody: String,
        isOfftopic: Boolean
    ): DataResult<Comment> {
        val publishMutation = PublishThreadCommentMutation(
            id = Optional.presentIfNotNull(id),
            threadId = topicId,
            parentCommentId = Optional.presentIfNotNull(parentCommentId),
            commentBody = commentBody
        )

        val response = apolloClient.mutation(publishMutation)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return response.asDataResult { data ->
            data.SaveThreadComment?.aLThreadComment?.toDomain()
                ?: throw NoSuchElementException("Couldn't Publish a Comment")
        }
    }

    override suspend fun deleteComment(id: Int): DataResult<Boolean> {
        val deleteMutation = DeleteThreadCommentMutation(
            commentId = id
        )

        val response = apolloClient.mutation(deleteMutation)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return response.asDataResult { data ->
            data.DeleteThreadComment?.deleted
                ?: throw NoSuchElementException("Couldn't Publish a Comment")
        }
    }
}