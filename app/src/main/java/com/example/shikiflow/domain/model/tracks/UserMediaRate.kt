package com.example.shikiflow.domain.model.tracks

import com.example.shikiflow.domain.model.track.UserRateStatus
import kotlin.time.Instant

data class UserMediaRate(
    val rateId: Int,
    val mediaId: Int,
    val rateStatus: UserRateStatus,
    val progress: Int,
    val progressVolumes: Int,
    val repeat: Int,
    val textNotes: String?,
    val score: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)
