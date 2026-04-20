package com.example.shikiflow.data.datasource.anilist

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.example.graphql.anilist.MediaThreadsQuery
import com.example.graphql.anilist.TopicCommentQuery
import com.example.graphql.anilist.TopicCommentsQuery
import com.example.shikiflow.data.datasource.CommentsDataSource
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.findComment
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.toAnilistThreadSort
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.toDomain
import com.example.shikiflow.di.annotations.AnilistApollo
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.sort.ThreadType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.utils.AnilistUtils.toResult
import javax.inject.Inject

class AnilistThreadsDataSource @Inject constructor(
    @param:AnilistApollo private val apolloClient: ApolloClient
): CommentsDataSource {
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

            data
                .asSequence()
                .mapNotNull { it?.findComment(commentId) }
                .firstOrNull()
                ?: throw NoSuchElementException("No Comment with ID: $commentId")
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
}