package com.example.shikiflow.data.local.entity.mediatrack

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shikiflow.domain.model.track.UserRateStatus
import kotlin.time.Instant

@Entity(tableName = "media_track")
data class MediaTrackEntity(
    val id: Int,
    val status: UserRateStatus,
    val progress: Int,
    val progressVolumes: Int?,
    val repeat: Int,
    val score: Int,
    val text: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    @PrimaryKey val mediaId: Int
)
