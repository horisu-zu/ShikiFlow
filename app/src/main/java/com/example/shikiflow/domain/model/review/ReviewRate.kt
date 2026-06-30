package com.example.shikiflow.domain.model.review

data class ReviewRate(
    val id: Int,
    val userRating: ReviewRating,
    val rating: Int,
    val ratingAmount: Int
)
