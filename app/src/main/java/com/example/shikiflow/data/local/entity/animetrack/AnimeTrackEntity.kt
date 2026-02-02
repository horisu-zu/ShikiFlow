package com.example.shikiflow.data.local.entity.animetrack

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.anime.AnimeUserTrack
import kotlin.time.Instant

@Entity(tableName = "anime_track")
data class AnimeTrackEntity(
    val id: Int,
    val status: UserRateStatus,
    val episodes: Int,
    val rewatches: Int,
    val score: Int,
    val text: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    @PrimaryKey val animeId: Int
) {
    companion object {
        fun AnimeUserTrack.toDto(): AnimeTrackEntity {
            return AnimeTrackEntity(
                id = this.id,
                status = this.status,
                episodes = this.episodes,
                rewatches = this.rewatches,
                score = this.score,
                text = this.text,
                createdAt = Instant.parse(this.createdAt.toString()),
                updatedAt = Instant.parse(this.updatedAt.toString()),
                animeId = this.animeId
            )
        }

        fun AnimeTrackEntity.toDomain(): AnimeUserTrack {
            return AnimeUserTrack(
                id = this.id,
                status = this.status,
                episodes = this.episodes,
                rewatches = this.rewatches,
                score = this.score,
                text = this.text,
                createdAt = this.createdAt,
                updatedAt = this.updatedAt,
                animeId = this.animeId,
            )
        }
    }
}