package com.example.shikiflow.domain.model.review

import com.example.shikiflow.domain.model.user.UserShort

data class ReviewShort(
    val id: Int,
    val title: String,
    val score: Int,
    val author: UserShort,
    val likesCount: Int,
    val ratingAmount: Int
)
