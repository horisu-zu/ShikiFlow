package com.example.shikiflow.domain.model.track.anime

import com.example.shikiflow.domain.model.track.UserRateStatus
import kotlin.time.Instant

data class AnimeUserTrack(
    val id: Int,
    val status: UserRateStatus,
    val episodes: Int,
    val rewatches: Int,
    val score: Int,
    val text: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val animeId: Int
)