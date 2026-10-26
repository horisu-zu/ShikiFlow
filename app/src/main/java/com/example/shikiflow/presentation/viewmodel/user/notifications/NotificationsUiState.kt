package com.example.shikiflow.presentation.viewmodel.user.notifications

import com.example.shikiflow.presentation.screen.more.notifications.NotificationTypeGroup

data class NotificationsUiState(
    val currentGroup: NotificationTypeGroup = NotificationTypeGroup.ALL
)
