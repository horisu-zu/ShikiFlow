package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.media_details.Genre
import com.example.shikiflow.domain.model.media_details.MediaTagEnum
import com.example.shikiflow.domain.model.media_details.MediaTitle
import com.example.shikiflow.domain.model.track.UserRateStatus

data class MediaComparison(
    val id: Int,
    val title: MediaTitle?,
    val synonyms: List<String>,
    val imageUrl: String?,
    val currentUserScore: ShortUserRateData?,
    val targetUserScore: ShortUserRateData?,
    val genres: List<Genre>,
    val tags: List<MediaTagEnum>
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