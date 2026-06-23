package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.common.ScoreFormat
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class User(
    val id: Int = 0,
    val avatarUrl: String = "",
    val nickname: String = "",
    val lastOnlineAt: Instant? = null,
    val profileBannerUrl: String? = null,
    val isFollowing: Boolean? = null,
    val isFollower: Boolean? = null,
    val scoreFormat: ScoreFormat = ScoreFormat.POINT_10
)

data class UserFollow(
    val isFollowing: Boolean,
    val isFollower: Boolean? = null
)