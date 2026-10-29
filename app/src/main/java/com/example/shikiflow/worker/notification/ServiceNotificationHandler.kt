package com.example.shikiflow.worker.notification

import android.content.Context
import com.example.shikiflow.R
import com.example.shikiflow.domain.repository.NotificationRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.presentation.common.mappers.NotificationMapper.displayValue
import com.example.shikiflow.presentation.screen.more.notifications.NotificationTypeGroup
import com.example.shikiflow.utils.notifications.NotificationUtils.showNotification
import com.example.shikiflow.utils.result.DataResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class ServiceNotificationHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationRepository: NotificationRepository,
    private val settingsRepository: SettingsRepository
): NotificationHandler {

    companion object {
        const val SERVICE_NOTIFICATION_CHANNEL_ID = "service_notification_channel"
        const val SERVICE_NOTIFICATION_GROUP_ID = "service_notification_group"
    }

    override suspend fun checkAndNotify() {
        val notificationPreferences = settingsRepository.userSettingsFlow
            .mapNotNull { userSettings -> userSettings?.notificationPreferences }
            .first()

        if (notificationPreferences.values.none { it }) return

        val resources = context.resources
        val lastCreatedAt = settingsRepository.notificationSettingsFlow
            .map { it.lastNotificationTimestamp }
            .first()

        when (val result = notificationRepository.getUnreadNotificationsCount()) {
            is DataResult.Success -> {
                notificationRepository.getNotifications(
                    page = 1,
                    perPage = result.data,
                    typeGroup = NotificationTypeGroup.ALL,
                    resetCount = false
                ).fold(
                    onSuccess = { data ->
                        val filteredData = data.filter { notification ->
                            notification.type in notificationPreferences.filter { it.value } &&
                            notification.timestamp > lastCreatedAt
                        }

                        if (filteredData.size > 1) {
                            context.showNotification(
                                notificationId = 0,
                                channelId = SERVICE_NOTIFICATION_CHANNEL_ID,
                                title = resources.getQuantityString(
                                    R.plurals.service_notifications_group_label,
                                    filteredData.size
                                ),
                                text = "",
                                group = SERVICE_NOTIFICATION_GROUP_ID,
                                isGroupSummary = true
                            )
                        }

                        filteredData.forEach { notification ->
                            context.showNotification(
                                notificationId = notification.id,
                                channelId = SERVICE_NOTIFICATION_CHANNEL_ID,
                                group = SERVICE_NOTIFICATION_GROUP_ID,
                                title = resources.getString(notification.type.displayValue()),
                                text = ""
                            )
                        }

                        settingsRepository.saveNotificationTimestamp(
                            instant = data.first().timestamp
                        )
                    },
                    onFailure = { throwable -> throw IllegalStateException(throwable) }
                )
            }
            is DataResult.Error -> { throw IllegalStateException(result.message) }
            else -> { /**/ }
        }
    }
}