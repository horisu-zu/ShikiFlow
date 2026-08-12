package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.thread.Like
import com.example.shikiflow.domain.model.thread.LikeableType
import com.example.shikiflow.domain.model.user.activity.ActivityReply
import com.example.shikiflow.domain.model.user.activity.UserActivity
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getSingleActivity(
        activityId: Int
    ): Flow<DataResult<UserActivity>>

    suspend fun submitActivity(
        id: Int?,
        recipientId: Int?,
        body: String
    ): DataResult<UserActivity>

    suspend fun deleteActivity(
        id: Int
    ): DataResult<Boolean>

    fun getActivityReplies(
        activityId: Int,
        page: Int,
        limit: Int = 15
    ): Flow<PagedResult<ActivityReply>>

    suspend fun submitActivityReply(
        id: Int?,
        activityId: Int,
        body: String
    ): DataResult<ActivityReply>

    suspend fun deleteActivityReply(
        id: Int
    ): DataResult<Boolean>

    suspend fun toggleLike(
        id: Int,
        type: LikeableType
    ): DataResult<Like>
}