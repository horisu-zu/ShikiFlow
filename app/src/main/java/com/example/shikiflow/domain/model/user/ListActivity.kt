package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlin.time.Instant

sealed interface UserActivity

data class ListActivity(
    val id: Int,
    val mediaId: Int,
    val mediaType: MediaType?,
    val title: String,
    val coverImage: String,
    val status: UserRateStatus,
    val progress: List<Int>,
    val progressVolumes: List<Int> = emptyList(),
    val scoreChange: Pair<Int, Int?>? = null,
    val createdAt: Instant
) : UserActivity

data class TextActivity(
    val id: Int,
    val text: String,
    val user: User,
    val createdAt: Instant
) : UserActivity

data class MessageActivity(
    val id: Int,
    val text: String,
    val messenger: User,
    val recipient: User,
    val createdAt: Instant
) : UserActivity