package com.example.shikiflow.data.datasource

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.notification.Notification
import com.example.shikiflow.presentation.screen.more.notifications.NotificationTypeGroup
import com.example.shikiflow.utils.result.DataResult
import kotlinx.coroutines.flow.Flow

interface NotificationsDataSource {
    suspend fun getUnreadNotificationsCount(): DataResult<Int>

    suspend fun getNotifications(
        page: Int,
        perPage: Int,
        typeGroup: NotificationTypeGroup,
        resetCount: Boolean
    ): Result<List<Notification>>

    fun getNotifications(
        typeGroup: NotificationTypeGroup,
        resetCount: Boolean
    ): Flow<PagingData<Notification>>
}