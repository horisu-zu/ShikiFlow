package com.example.shikiflow.domain.model.track.manga

import com.example.shikiflow.domain.model.track.UserRateStatus
import kotlin.time.Instant

data class MangaUserTrack(
    val id: Int,
    val status: UserRateStatus,
    val chapters: Int,
    val volumes: Int,
    val rewatches: Int,
    val score: Int,
    val text: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val mangaId: Int
)
