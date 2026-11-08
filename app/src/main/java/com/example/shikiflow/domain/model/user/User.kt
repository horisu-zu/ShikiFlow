package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.notification.NotificationType
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.serialization.Serializable

sealed interface BaseUser {
    val id: Int
    val nickname: String
    val avatarUrl: String
    val profileBannerUrl: String?
}

@Serializable
data class UserShort(
    override val id: Int,
    override val nickname: String,
    override val avatarUrl: String,
    override val profileBannerUrl: String? = null
): BaseUser

data class CurrentUser(
    override val id: Int,
    override val nickname: String,
    override val avatarUrl: String,
    override val profileBannerUrl: String? = null,
    val scoreFormat: ScoreFormat = ScoreFormat.POINT_10,
    val customLists: Map<MediaType, List<String>> = emptyMap(),
    val notificationPreferences: Map<NotificationType, Boolean> = emptyMap(),
    val unreadNotificationsCount: Int? = null
): BaseUser

data class UserFollow(
    val isFollowing: Boolean,
    val isFollower: Boolean? = null
)