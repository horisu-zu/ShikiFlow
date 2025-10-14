package com.example.shikiflow.domain.model.tracks

import com.example.graphql.AnimeDetailsQuery
import com.example.graphql.MangaDetailsQuery
import com.example.graphql.type.UserRateStatusEnum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRateResponse(
    val id: Long,
    @SerialName("user_id") val userId: Long,
    @SerialName("target_id") val targetId: Long,
    @SerialName("target_type") val targetType: String,
    val score: Int,
    val status: String,
    val rewatches: Int,
    val episodes: Int? = null,
    val volumes: Int? = null,
    val chapters: Int? = null,
    val text: String? = null,
    @SerialName("text_html") val textHtml: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
) {
    companion object {
        fun UserRateResponse.toMangaUserRate(): MangaDetailsQuery.UserRate {
            return MangaDetailsQuery.UserRate(
                id = this.id.toString(),
                status = UserRateStatusEnum.valueOf(this.status),
                chapters = this.chapters ?: 0,
                volumes = this.volumes ?: 0,
                score = this.score,
                rewatches = this.rewatches,
                createdAt = this.createdAt,
                updatedAt = this.updatedAt
            )
        }

        fun UserRateResponse.toAnimeUserRate(): AnimeDetailsQuery.UserRate {
            return AnimeDetailsQuery.UserRate(
                id = this.id.toString(),
                status = UserRateStatusEnum.valueOf(this.status),
                episodes = this.episodes ?: 0,
                score = this.score,
                rewatches = this.rewatches,
                createdAt = this.createdAt,
                updatedAt = this.updatedAt
            )
        }
    }
}
