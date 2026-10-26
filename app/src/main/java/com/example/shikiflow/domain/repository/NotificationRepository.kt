package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.notification.Notification
import com.example.shikiflow.presentation.screen.more.notifications.NotificationTypeGroup
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun getUnreadNotificationsCount(): DataResult<Int>

    suspend fun getNotifications(
        page: Int,
        perPage: Int = 24,
        typeGroup: NotificationTypeGroup,
        resetCount: Boolean
    ): Flow<PagedResult<Notification>>

    fun getNotifications(
        typeGroup: NotificationTypeGroup,
        resetCount: Boolean
    ): Flow<PagingData<Notification>>
}