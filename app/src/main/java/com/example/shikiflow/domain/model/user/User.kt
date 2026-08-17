package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class User(
    val id: Int = 0,
    val avatarUrl: String = "",
    val nickname: String = "",
    val lastOnlineAt: Instant? = null,
    val profileBannerUrl: String? = null,
    val scoreFormat: ScoreFormat = ScoreFormat.POINT_10,
    val customLists: Map<MediaType, List<String>> = emptyMap()
)

data class UserFollow(
    val isFollowing: Boolean,
    val isFollower: Boolean? = null
)