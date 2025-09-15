package com.example.shikiflow.data.local.entity.animetrack

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.graphql.fragment.AnimeUserRateWithModel
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.domain.model.tracks.UserRateResponse
import kotlin.time.Instant

@Entity(tableName = "anime_track")
data class AnimeTrackEntity(
    @PrimaryKey val id: String,
    val status: UserRateStatusEnum,
    val episodes: Int,
    val rewatches: Int,
    val score: Int,
    val text: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val animeId: String
) {
    companion object {
        fun AnimeUserRateWithModel.toEntity(): AnimeTrackEntity {
            return AnimeTrackEntity(
                id = this.id,
                status = this.status,
                episodes = this.episodes,
                rewatches = this.rewatches,
                score = this.score,
                text = this.text,
                createdAt = Instant.parse(this.createdAt.toString()),
                updatedAt = Instant.parse(this.updatedAt.toString()),
                animeId = this.anime?.animeShort?.id.toString()
            )
        }

        fun UserRateResponse.toEntity(): AnimeTrackEntity {
            return AnimeTrackEntity(
                id = this.id.toString(),
                status = UserRateStatusEnum.valueOf(this.status),
                episodes = this.episodes ?: 0,
                rewatches = this.rewatches,
                score = this.score,
                text = this.text,
                createdAt = Instant.parse(this.createdAt),
                updatedAt = Instant.parse(this.updatedAt),
                animeId = this.targetId.toString()
            )
        }
    }
}