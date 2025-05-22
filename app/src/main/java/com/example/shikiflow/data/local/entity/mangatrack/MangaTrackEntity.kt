package com.example.shikiflow.data.local.entity.mangatrack

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.graphql.fragment.MangaUserRateWithModel
import com.example.graphql.type.UserRateStatusEnum
import kotlinx.datetime.Instant

@Entity(tableName = "manga_track")
data class MangaTrackEntity(
    @PrimaryKey val id: String,
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
        fun MangaUserRateWithModel.toEntity(): MangaTrackEntity {
            return MangaTrackEntity(
                id = this.id,
                status = this.status,
                chapters = this.chapters,
                volumes = this.volumes,
                rewatches = this.rewatches,
                score = this.score,
                text = this.text,
                createdAt = Instant.parse(this.createdAt.toString()),
                updatedAt = Instant.parse(this.updatedAt.toString()),
                mangaId = this.manga?.mangaShort?.id.toString()
            )
        }
    }
}
