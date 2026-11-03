package com.example.shikiflow.domain.model.user.activity

import com.example.shikiflow.domain.model.media_details.MediaTitle
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType

sealed interface NotificationActivity {
    val id: Int
    val type: ActivityType
}

data class ListNotificationActivity(
    override val id: Int,
    override val type: ActivityType = ActivityType.LIST,
    val mediaType: MediaType,
    val title: MediaTitle,
    val coverImage: String,
    val status: UserRateStatus,
    val progress: List<Int>,
    val progressVolumes: List<Int> = emptyList()
): NotificationActivity

data class TextNotificationActivity(
    override val id: Int,
    override val type: ActivityType,
    val text: String
): NotificationActivity