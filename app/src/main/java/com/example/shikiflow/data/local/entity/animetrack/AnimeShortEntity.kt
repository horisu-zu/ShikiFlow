package com.example.shikiflow.data.local.entity.animetrack

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.graphql.shikimori.fragment.AnimeShort
import com.example.shikiflow.data.local.entity.PosterEntity
import com.example.shikiflow.data.local.entity.PosterEntity.Companion.toDomain
import com.example.shikiflow.data.local.entity.PosterEntity.Companion.toDto
import com.example.shikiflow.data.local.entity.PosterEntity.Companion.toEntity
import com.example.shikiflow.data.local.entity.ReleaseDateEntity
import com.example.shikiflow.data.local.entity.ReleaseDateEntity.Companion.toDomain
import com.example.shikiflow.data.local.entity.ReleaseDateEntity.Companion.toDto
import com.example.shikiflow.data.local.entity.ReleaseDateEntity.Companion.toEntity
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaStatusMapper.toDomain
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.track.anime.AnimeShortData
import kotlin.time.Instant

@Entity(tableName = "anime_short")
data class AnimeShortEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val japanese: String?,
    val kind: MediaFormat?,
    val score: Float?,
    val status: MediaStatus?,
    val episodes: Int,
    val episodesAired: Int,
    val nextEpisodeAt: Instant?,
    val duration: Int?,
    @Embedded(prefix = "aired_on")
    val airedOn: ReleaseDateEntity?,
    @Embedded(prefix = "released_on")
    val releasedOn: ReleaseDateEntity?,
    @Embedded(prefix = "poster")
    val poster: PosterEntity?
) {
    companion object {
        fun AnimeShort.toEntity(): AnimeShortEntity {
            return AnimeShortEntity(
                id = this.id.toInt(),
                name = this.name,
                japanese = this.japanese,
                kind = this.kind?.toDomain(),
                score = this.score?.toFloat(),
                status = this.status?.toDomain(),
                episodes = this.episodes,
                episodesAired = this.episodesAired,
                nextEpisodeAt = this.nextEpisodeAt?.let { Instant.parse(it.toString()) },
                duration = this.duration,
                airedOn = this.airedOn?.dateShort?.toEntity(),
                releasedOn = this.releasedOn?.dateShort?.toEntity(),
                poster = this.poster?.toEntity()
            )
        }

        fun AnimeShortEntity.toDomain(): AnimeShortData {
            return AnimeShortData(
                id = this.id,
                name = this.name,
                japanese = this.japanese,
                kind = this.kind,
                score = this.score,
                status = this.status,
                episodes = this.episodes,
                episodesAired = this.episodesAired,
                nextEpisodeAt = this.nextEpisodeAt,
                duration = this.duration,
                airedOn = this.airedOn?.toDomain(),
                releasedOn = this.releasedOn?.toDomain(),
                studios = emptyList(),
                genres = emptyList(),
                poster = this.poster?.toDomain()
            )
        }

        fun AnimeShortData.toDto(): AnimeShortEntity {
            return AnimeShortEntity(
                id = this.id,
                name = this.name,
                japanese = this.japanese,
                kind = this.kind,
                score = this.score,
                status = this.status,
                episodes = this.episodes,
                episodesAired = this.episodesAired,
                nextEpisodeAt = this.nextEpisodeAt,
                duration = this.duration,
                airedOn = this.airedOn?.toDto(),
                releasedOn = this.releasedOn?.toDto(),
                poster = this.poster?.toDto()
            )
        }
    }
}
