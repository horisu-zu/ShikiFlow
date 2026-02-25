package com.example.shikiflow.data.datasource.anilist

import com.apollographql.apollo.ApolloClient
import com.example.graphql.anilist.MediaThreadsQuery
import com.example.graphql.anilist.TopicCommentQuery
import com.example.graphql.anilist.TopicCommentsQuery
import com.example.shikiflow.data.datasource.CommentsDataSource
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.findComment
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.toAnilistThreadSort
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.toDomain
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.thread.ThreadSort
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AnilistThreadsDataSource @Inject constructor(
    private val apolloClient: ApolloClient
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

        return try {
            val commentsResponse = apolloClient.query(topicCommentsQuery).execute()

            val commentsResult = commentsResponse.data
                ?.Page
                ?.threadComments
                ?.mapNotNull { it?.toDomain() }
                ?: emptyList()

            Result.success(commentsResult)
        } catch (e: IOException) {
            Result.failure(Exception("${e.message}: Missing Internet Connection"))
        } catch (e: HttpException) {
            Result.failure(e)
        }
    }

    override suspend fun getCommentById(commentId: Int): Comment {
        val response = apolloClient
            .query(TopicCommentQuery(commentId))
            .execute()

        val branches = response.data
            ?.ThreadComment
            .orEmpty()
            .map { it?.toDomain() }

        return branches
            .asSequence()
            .mapNotNull { it?.findComment(commentId) }
            .firstOrNull()
            ?: throw NoSuchElementException("No Comment with ID: $commentId")
    }

    override suspend fun getMediaThreads(
        mediaId: Int,
        page: Int,
        limit: Int,
        threadSort: ThreadSort
    ): Result<List<Thread>> {
        val threadsQuery = MediaThreadsQuery(
            mediaId = mediaId,
            page = page,
            perPage = limit,
            sort = threadSort.toAnilistThreadSort()
        )

        return try {
            val response = apolloClient.query(threadsQuery).execute()

            val result = response.data
                ?.Page
                ?.threads
                ?.mapNotNull { it?.aLThread?.toDomain() }
                ?: emptyList()

            Result.success(result)
        } catch (e: IOException) {
            Result.failure(Exception("${e.message}: Missing Internet Connection"))
        } catch (e: HttpException) {
            Result.failure(e)
        }
    }
}