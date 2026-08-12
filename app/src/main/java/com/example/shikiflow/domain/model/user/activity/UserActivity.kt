package com.example.shikiflow.domain.model.user.activity

import com.example.shikiflow.domain.model.media_details.MediaTitle
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import kotlin.time.Instant

sealed interface UserActivity {
    val id: Int
    val type: ActivityType
    val createdAt: Instant
    val likeCount: Int?
    val replyCount: Int?
    val isLiked: Boolean?
}

data class ListActivity(
    override val id: Int,
    override val type: ActivityType = ActivityType.LIST,
    val userId: Int,
    val mediaId: Int,
    val mediaType: MediaType?,
    val title: MediaTitle,
    val coverImage: String,
    val status: UserRateStatus,
    val progress: List<Int>,
    val progressVolumes: List<Int> = emptyList(),
    val scoreChange: Pair<Int, Int?>? = null,
    override val createdAt: Instant,
    override val likeCount: Int? = null,
    override val replyCount: Int? = null,
    override val isLiked: Boolean? = null
) : UserActivity

data class TextActivity(
    override val id: Int,
    override val type: ActivityType = ActivityType.TEXT,
    val text: String,
    val markdownText: String,
    val user: User,
    override val createdAt: Instant,
    override val likeCount: Int? = null,
    override val replyCount: Int? = null,
    override val isLiked: Boolean? = null
) : UserActivity

data class MessageActivity(
    override val id: Int,
    override val type: ActivityType = ActivityType.MESSAGE,
    val text: String,
    val markdownText: String,
    val messenger: User,
    val recipient: User,
    override val createdAt: Instant,
    override val likeCount: Int? = null,
    override val replyCount: Int? = null,
    override val isLiked: Boolean? = null
) : UserActivity

enum class ActivityType {
    LIST,
    TEXT,
    MESSAGE
}

object UserActivityMapper {
    fun UserActivity.updateLike(likeCount: Int?, isLiked: Boolean?): UserActivity =
        when (this) {
            is ListActivity -> copy(likeCount = likeCount, isLiked = isLiked)
            is MessageActivity -> copy(likeCount = likeCount, isLiked = isLiked)
            is TextActivity -> copy(likeCount = likeCount, isLiked = isLiked)
        }
}