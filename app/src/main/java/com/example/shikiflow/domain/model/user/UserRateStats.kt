package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType

data class UserRateStats(
    val mediaStats: Map<MediaType, MediaTypeStats>
)

data class MediaTypeStats(
    val count: Int,
    val averageScore: Double,
    val statusesStats: Map<UserRateStatus, Int>,
    val scoreStats: Map<Int, Int>
)
