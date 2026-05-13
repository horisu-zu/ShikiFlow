package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.media_details.MediaTitle
import com.example.shikiflow.domain.model.track.UserRateStatus

data class MediaComparison(
    val id: Int,
    val title: MediaTitle?,
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