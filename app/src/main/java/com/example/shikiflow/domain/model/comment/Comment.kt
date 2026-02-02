package com.example.shikiflow.domain.model.comment

import com.example.shikiflow.domain.model.user.User
import kotlin.time.Instant

sealed interface Comment {
    val id: Int
    val commentBody: String
    val dateTime: Instant
    val sender: User?
}

data class ShikiComment(
    override val id: Int,
    override val commentBody: String,
    override val dateTime: Instant,
    override val sender: User?,
    val isOfftopic: Boolean
): Comment

data class ALComment(
    override val id: Int,
    override val commentBody: String,
    override val dateTime: Instant,
    override val sender: User?,
    val childComments: List<ALComment>,
    val likesCount: Int
): Comment
