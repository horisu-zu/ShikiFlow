package com.example.shikiflow.domain.model.settings

data class NotificationSettings(
    val showNotifications: Boolean = false,
    val showAiringNotifications: Boolean = true,
    val showUpcomingNotifications: Boolean = true,
    val airingDelay: Float = 0f,
    val timeRange: Pair<Float, Float> = 0f to 1440f
)
