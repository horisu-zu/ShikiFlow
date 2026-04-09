package com.example.shikiflow.domain.model.track.media

import androidx.room.PrimaryKey
import com.example.shikiflow.domain.model.track.UserRateStatus
import kotlin.time.Instant

data class MediaUserTrack(
    @PrimaryKey val mediaId: Int,
    val id: Int,
    val status: UserRateStatus,
    val progress: Int,
    val progressVolumes: Int?,
    val repeat: Int,
    val score: Int,
    val text: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)
