package com.example.shikiflow.domain.model.review

import com.example.shikiflow.domain.model.user.User

data class ReviewShort(
    val id: Int,
    val title: String,
    val score: Int,
    val author: User,
    val likesCount: Int,
    val ratingAmount: Int
)
