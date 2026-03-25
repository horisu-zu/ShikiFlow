package com.example.shikiflow.domain.model.user

import kotlin.time.Instant

sealed interface UserActivity

data class ListActivity(
    val id: Int,
    val mediaId: Int,
    val title: String,
    val coverImage: String,
    val description: String,
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