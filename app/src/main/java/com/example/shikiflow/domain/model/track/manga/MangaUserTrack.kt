package com.example.shikiflow.domain.model.track.manga

import com.example.graphql.type.UserRateStatusEnum
import kotlin.time.Instant

data class MangaUserTrack(
    val id: String,
    val status: UserRateStatusEnum,
    val chapters: Int,
    val volumes: Int,
    val rewatches: Int,
    val score: Int,
    val text: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val mangaId: String
)
