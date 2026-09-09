package com.example.shikiflow.utils.notifications

import android.content.Context
import com.example.shikiflow.R
import com.example.shikiflow.utils.notifications.NotificationUtils.createNotificationChannel
import com.example.shikiflow.worker.notification.AiringNotificationHandler.Companion.AIRING_CHANNEL_ID
import com.example.shikiflow.worker.notification.AiringNotificationHandler.Companion.AIRING_GROUP_ID
import com.example.shikiflow.worker.notification.UpcomingNotificationHandler.Companion.UPCOMING_CHANNEL_ID

object NotificationChannels {
    private val airingChannels = listOf(
        NotificationChannelData(
            id = AIRING_CHANNEL_ID,
            nameRes = R.string.airing_notifications_system_label,
            descriptionRes = R.string.airing_episode_notification_system_description,
            groupId = AIRING_GROUP_ID,
            groupNameRes = R.string.anime_streaming_notifications_group_system_label
        ),
        NotificationChannelData(
            id = UPCOMING_CHANNEL_ID,
            nameRes = R.string.airing_premiere_notifications_system_label,
            descriptionRes = R.string.airing_premiere_notification_system_description,
            groupId = AIRING_GROUP_ID,
            groupNameRes = R.string.anime_streaming_notifications_group_system_label
        )
    )

    fun createChannels(context: Context) {
        val resources = context.resources

        airingChannels.forEach { channelData ->
            context.createNotificationChannel(
                id = channelData.id,
                name = resources.getString(channelData.nameRes),
                description = resources.getString(channelData.descriptionRes),
                groupId = channelData.groupId,
                groupName = resources.getString(channelData.groupNameRes)
            )
        }
    }
}

private data class NotificationChannelData(
    val id: String,
    val nameRes: Int,
    val descriptionRes: Int,
    val groupId: String,
    val groupNameRes: Int
)