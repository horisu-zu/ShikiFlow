package com.example.shikiflow.data.datasource.anilist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.fetchPolicy
import com.example.graphql.anilist.NotificationsQuery
import com.example.graphql.anilist.UnreadNotificationsCountQuery
import com.example.shikiflow.data.datasource.NotificationsDataSource
import com.example.shikiflow.data.local.source.GenericPagingSource
import com.example.shikiflow.data.mapper.anilist.AnilistNotificationsMapper.toAnilistTypes
import com.example.shikiflow.data.mapper.anilist.AnilistNotificationsMapper.toDomainNotification
import com.example.shikiflow.di.annotations.AnilistApollo
import com.example.shikiflow.domain.model.notification.Notification
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.presentation.screen.more.notifications.NotificationTypeGroup
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnilistNotificationsDataSource @Inject constructor(
    @param:AnilistApollo private val apolloClient: ApolloClient
): NotificationsDataSource, BaseNetworkRepository() {
    override suspend fun getUnreadNotificationsCount(): DataResult<Int> {
        val unreadQuery = UnreadNotificationsCountQuery()

        val response = apolloClient.query(unreadQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()
            .asDataResult { data ->
                data.Viewer?.unreadNotificationCount
                    ?: throw IllegalStateException("Empty Response")
            }

        return response
    }

    override suspend fun getNotifications(
        page: Int,
        perPage: Int,
        typeGroup: NotificationTypeGroup,
        resetCount: Boolean
    ): Flow<PagedResult<Notification>> {
        val notificationsQuery = NotificationsQuery(
            page = page,
            perPage = perPage,
            typeIn = when (typeGroup) {
                NotificationTypeGroup.ALL -> Optional.absent()
                else -> Optional.present(typeGroup.toAnilistTypes())
            },
            resetCount = resetCount
        )

        val result = apolloClient.query(notificationsQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .toFlow()
            .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) { data ->
                data.Page?.notifications?.mapNotNull { notification ->
                    notification?.toDomainNotification()
                } ?: emptyList()
            }

        return result
    }

    override fun getNotifications(
        typeGroup: NotificationTypeGroup,
        resetCount: Boolean
    ): Flow<PagingData<Notification>> {
        return Pager(
            config = PagingConfig(
                pageSize = 24,
                enablePlaceholders = true,
                prefetchDistance = 9,
                initialLoadSize = 24
            ),
            pagingSourceFactory = {
                GenericPagingSource(
                    method = { page, limit ->
                        getPagedNotifications(page, limit, typeGroup, resetCount)
                    }
                )
            }
        ).flow
    }

    private suspend fun getPagedNotifications(
        page: Int,
        limit: Int,
        typeGroup: NotificationTypeGroup,
        resetCount: Boolean
    ): Result<List<Notification>> {
        val notificationsQuery = NotificationsQuery(
            page = page,
            perPage = limit,
            typeIn = when (typeGroup) {
                NotificationTypeGroup.ALL -> Optional.absent()
                else -> Optional.present(typeGroup.toAnilistTypes())
            },
            resetCount = resetCount
        )

        val result = apolloClient.query(notificationsQuery)
            .fetchPolicy(FetchPolicy.NetworkFirst)
            .execute()
            .toResult()
            .map { data ->
                data.Page?.notifications?.mapNotNull { notification ->
                    notification?.toDomainNotification()
                } ?: emptyList()
            }

        return result
    }
}