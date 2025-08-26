package com.example.shikiflow.domain.model.track.anime

import com.example.graphql.fragment.AnimeUserRateWithModel
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.domain.model.tracks.UserRateResponse
import kotlinx.datetime.Instant

data class AnimeUserTrack(
    val id: String,
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
        fun AnimeUserRateWithModel.toEntity(): AnimeUserTrack {
            return AnimeUserTrack(
                id = this.id,
                status = this.status,
                episodes = this.episodes,
                rewatches = this.rewatches,
                score = this.score,
                text = this.text,
                createdAt = Instant.Companion.parse(this.createdAt.toString()),
                updatedAt = Instant.Companion.parse(this.updatedAt.toString()),
                animeId = this.anime?.animeShort?.id.toString()
            )
        }

        fun UserRateResponse.toEntity(): AnimeUserTrack {
            return AnimeUserTrack(
                id = this.id.toString(),
                status = UserRateStatusEnum.valueOf(this.status),
                episodes = this.episodes ?: 0,
                rewatches = this.rewatches,
                score = this.score,
                text = this.text,
                createdAt = Instant.Companion.parse(this.createdAt),
                updatedAt = Instant.Companion.parse(this.updatedAt),
                animeId = this.targetId.toString()
            )
        }
    }
}