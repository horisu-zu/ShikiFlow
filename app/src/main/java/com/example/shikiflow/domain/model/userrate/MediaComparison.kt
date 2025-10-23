package com.example.shikiflow.domain.model.userrate

import com.example.graphql.type.UserRateStatusEnum

data class MediaComparison(
    val mediaId: String,
    val mediaTitle: String,
    val mediaImage: String?,
    val currentUserScore: ShortUserRateData?,
    val targetUserScore: ShortUserRateData?
)

data class ShortUserRateData(
    val userScore: Int,
    val status: UserRateStatusEnum
)

enum class ComparisonType {
    SHARED,
    CURRENT_USER_ONLY,
    TARGET_USER_ONLY
}