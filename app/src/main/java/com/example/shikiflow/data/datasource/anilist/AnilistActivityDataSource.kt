package com.example.shikiflow.data.datasource.anilist

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.fetchPolicy
import com.example.graphql.anilist.ActivityRepliesQuery
import com.example.graphql.anilist.DeleteActivityMutation
import com.example.graphql.anilist.DeleteActivityReplyMutation
import com.example.graphql.anilist.PublishActivityReplyMutation
import com.example.graphql.anilist.PublishTextActivityMutation
import com.example.graphql.anilist.SingleUserActivityQuery
import com.example.graphql.anilist.ToggleLikeMutation
import com.example.shikiflow.data.datasource.ActivityDataSource
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.toALType
import com.example.shikiflow.data.mapper.anilist.AnilistThreadsMapper.toDomainLike
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toDomainReply
import com.example.shikiflow.di.annotations.AnilistApollo
import com.example.shikiflow.domain.model.thread.Like
import com.example.shikiflow.domain.model.thread.LikeableType
import com.example.shikiflow.domain.model.user.activity.ActivityReply
import com.example.shikiflow.domain.model.user.activity.TextActivity
import com.example.shikiflow.domain.model.user.activity.UserActivity
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnilistActivityDataSource @Inject constructor(
    @param:AnilistApollo private val apolloClient: ApolloClient
): ActivityDataSource, BaseNetworkRepository() {
    override fun getSingleActivity(
        activityId: Int
    ): Flow<DataResult<UserActivity>> {
        val activityQuery = SingleUserActivityQuery(
            activityId = activityId
        )

        val response = apolloClient.query(activityQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .toFlow()

        return response.asDataResult { data ->
            data.Activity?.let { activity ->
                activity.onListActivity?.aLListActivity?.toDomain() ?:
                activity.onMessageActivity?.aLMessageActivity?.toDomain() ?:
                activity.onTextActivity?.aLTextActivity?.toDomain()
            } ?: throw IllegalStateException("Error fetching activity with ID: $activityId")
        }
    }

    override suspend fun submitTextActivity(
        id: Int?,
        body: String
    ): DataResult<TextActivity> {
        val textActivityMutation = PublishTextActivityMutation(
            activityId = Optional.presentIfNotNull(id),
            textBody = body
        )

        val response = apolloClient.mutation(textActivityMutation)
            .fetchPolicy(FetchPolicy.NetworkOnly)
            .execute()

        return response.asDataResult { data ->
            data.SaveTextActivity?.aLTextActivity?.toDomain()
                ?: throw IllegalStateException("Couldn't Submit Text Activity")
        }
    }

    override suspend fun deleteActivity(id: Int): DataResult<Boolean> {
        val deleteActivityMutation = DeleteActivityMutation(id = id)

        val response = apolloClient.mutation(deleteActivityMutation)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return response.asDataResult { data ->
            data.DeleteActivity?.deleted
                ?: throw IllegalStateException("Couldn't Delete Activity Reply")
        }
    }

    override fun getActivityReplies(
        activityId: Int,
        page: Int,
        limit: Int
    ): Flow<PagedResult<ActivityReply>> {
        val activityRepliesQuery = ActivityRepliesQuery(
            activityId = activityId,
            page = page,
            limit = limit
        )

        val response = apolloClient.query(activityRepliesQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .toFlow()

        return response.asPagedResult(page = { it.Page?.pageInfo?.commonPage } ) { data ->
            data.Page?.activityReplies?.mapNotNull { reply ->
                reply?.aLActivityReply?.toDomainReply()
            } ?: emptyList()
        }
    }

    override suspend fun submitActivityReply(
        id: Int?,
        activityId: Int,
        body: String
    ): DataResult<ActivityReply> {
        val activityReplyMutation = PublishActivityReplyMutation(
            id = Optional.presentIfNotNull(id),
            activityId = activityId,
            text = body
        )

        val response = apolloClient.mutation(activityReplyMutation)
            .fetchPolicy(FetchPolicy.NetworkOnly)
            .execute()

        return response.asDataResult { data ->
            data.SaveActivityReply?.aLActivityReply?.toDomainReply()
                ?: throw IllegalStateException("Couldn't Submit Activity Reply")
        }
    }

    override suspend fun deleteActivityReply(id: Int): DataResult<Boolean> {
        val deleteReplyMutation = DeleteActivityReplyMutation(id = id)

        val response = apolloClient.mutation(deleteReplyMutation)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return response.asDataResult { data ->
            data.DeleteActivityReply?.deleted
                ?: throw IllegalStateException("Couldn't Delete Activity Reply")
        }
    }

    override suspend fun toggleLike(
        id: Int,
        type: LikeableType
    ): DataResult<Like> {
        val likeMutation = ToggleLikeMutation(
            likeableId = id,
            type = type.toALType()
        )

        val response = apolloClient.mutation(likeMutation)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()

        return response.asDataResult { data ->
            data.ToggleLikeV2?.toDomainLike(type)
                ?: throw IllegalStateException("No Activity/Reply with ID: $id")
        }
    }
}