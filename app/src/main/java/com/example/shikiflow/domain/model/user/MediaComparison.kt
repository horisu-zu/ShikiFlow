package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.track.UserRateStatus

data class MediaComparison(
    val mediaId: String,
    val mediaTitle: String,
    val mediaImage: String?,
    val currentUserScore: ShortUserRateData?,
    val targetUserScore: ShortUserRateData?
)

data class ShortUserRateData(
    val userScore: Int,
    val status: UserRateStatus
)

enum class ComparisonType {
    SHARED,
    CURRENT_USER_ONLY,
    TARGET_USER_ONLY
}