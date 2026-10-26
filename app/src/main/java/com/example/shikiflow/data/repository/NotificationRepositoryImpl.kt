package com.example.shikiflow.data.repository

import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.NotificationsDataSource
import com.example.shikiflow.di.annotations.AniList
import com.example.shikiflow.domain.model.notification.Notification
import com.example.shikiflow.domain.repository.NotificationRepository
import com.example.shikiflow.presentation.screen.more.notifications.NotificationTypeGroup
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    @param:AniList private val anilistDataSource: NotificationsDataSource
): NotificationRepository {
    override suspend fun getUnreadNotificationsCount(): DataResult<Int> =
        anilistDataSource.getUnreadNotificationsCount()

    override suspend fun getNotifications(
        page: Int,
        perPage: Int,
        typeGroup: NotificationTypeGroup,
        resetCount: Boolean
    ): Flow<PagedResult<Notification>> = anilistDataSource.getNotifications(page, perPage, typeGroup, resetCount)

    override fun getNotifications(
        typeGroup: NotificationTypeGroup,
        resetCount: Boolean
    ): Flow<PagingData<Notification>> = anilistDataSource.getNotifications(typeGroup, resetCount)
}