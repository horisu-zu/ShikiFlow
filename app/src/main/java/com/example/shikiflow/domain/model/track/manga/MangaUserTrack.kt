package com.example.shikiflow.domain.model.track.manga

import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.domain.model.tracks.UserRateResponse
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
) {
    companion object {
        fun UserRateResponse.toEntity(): MangaUserTrack {
            return MangaUserTrack(
                id = this.id.toString(),
                status = UserRateStatusEnum.valueOf(this.status),
                chapters = this.chapters ?: 0,
                volumes = this.volumes ?: 0,
                rewatches = this.rewatches,
                score = this.score,
                text = this.text,
                createdAt = Instant.parse(this.createdAt),
                updatedAt = Instant.parse(this.updatedAt),
                mangaId = this.targetId.toString()
            )
        }
    }
}
