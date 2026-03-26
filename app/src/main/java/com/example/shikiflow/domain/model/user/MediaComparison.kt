package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.track.UserRateStatus

data class MediaComparison(
    val id: Int,
    val title: String,
    val imageUrl: String?,
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