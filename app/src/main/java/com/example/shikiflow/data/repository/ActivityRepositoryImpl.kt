package com.example.shikiflow.data.repository

import com.example.shikiflow.data.datasource.ActivityDataSource
import com.example.shikiflow.di.annotations.AniList
import com.example.shikiflow.domain.model.thread.Like
import com.example.shikiflow.domain.model.thread.LikeableType
import com.example.shikiflow.domain.model.user.activity.ActivityReply
import com.example.shikiflow.domain.model.user.activity.UserActivity
import com.example.shikiflow.domain.repository.ActivityRepository
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    @param:AniList private val anilistUserDataSource: ActivityDataSource
): ActivityRepository {
    override fun getSingleActivity(
        activityId: Int
    ): Flow<DataResult<UserActivity>> = anilistUserDataSource.getSingleActivity(activityId)

    override suspend fun submitActivity(
        id: Int?,
        recipientId: Int?,
        body: String
    ): DataResult<UserActivity> = when (recipientId) {
        null -> anilistUserDataSource.submitTextActivity(id, body)
        else -> anilistUserDataSource.submitMessageActivity(id, recipientId, body)
    }

    override suspend fun deleteActivity(
        id: Int
    ): DataResult<Boolean> = anilistUserDataSource.deleteActivity(id)

    override fun getActivityReplies(
        activityId: Int,
        page: Int,
        limit: Int
    ): Flow<PagedResult<ActivityReply>> = anilistUserDataSource.getActivityReplies(activityId, page, limit)

    override suspend fun submitActivityReply(
        id: Int?,
        activityId: Int,
        body: String
    ): DataResult<ActivityReply> = anilistUserDataSource.submitActivityReply(id, activityId, body)

    override suspend fun deleteActivityReply(
        id: Int
    ): DataResult<Boolean> = anilistUserDataSource.deleteActivityReply(id)

    override suspend fun toggleLike(
        id: Int,
        type: LikeableType
    ): DataResult<Like> = anilistUserDataSource.toggleLike(id, type)
}