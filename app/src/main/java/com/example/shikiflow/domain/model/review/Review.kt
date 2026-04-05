package com.example.shikiflow.domain.model.review

import com.example.shikiflow.domain.model.user.User
import kotlin.time.Instant

data class Review(
    val id: Int,
    val title: String,
    val body: String,
    val score: Int,
    val author: User,
    val media: ReviewMedia?,
    val likesCount: Int,
    val ratingAmount: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)
