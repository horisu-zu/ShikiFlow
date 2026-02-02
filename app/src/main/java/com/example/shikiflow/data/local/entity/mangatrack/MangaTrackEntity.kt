package com.example.shikiflow.data.local.entity.mangatrack

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.manga.MangaUserTrack
import kotlin.time.Instant

@Entity(tableName = "manga_track")
data class MangaTrackEntity(
    val id: Int,
    val status: UserRateStatus,
    val chapters: Int,
    val volumes: Int,
    val rewatches: Int,
    val score: Int,
    val text: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    @PrimaryKey val mangaId: Int
) {
    companion object {
        fun MangaUserTrack.toDto(): MangaTrackEntity {
            return MangaTrackEntity(
                id = this.id,
                status = this.status,
                chapters = this.chapters,
                volumes = this.volumes,
                rewatches = this.rewatches,
                score = this.score,
                text = this.text,
                createdAt = this.createdAt,
                updatedAt = this.updatedAt,
                mangaId = this.mangaId
            )
        }

        fun MangaTrackEntity.toDomain(): MangaUserTrack {
            return MangaUserTrack(
                id = this.id,
                status = this.status,
                chapters = this.chapters,
                volumes = this.volumes,
                rewatches = this.rewatches,
                score = this.score,
                text = this.text,
                createdAt = this.createdAt,
                updatedAt = this.updatedAt,
                mangaId = this.mangaId
            )
        }
    }
}
