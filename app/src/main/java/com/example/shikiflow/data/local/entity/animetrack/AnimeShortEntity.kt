package com.example.shikiflow.data.local.entity.animetrack

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.graphql.fragment.AnimeShort
import com.example.graphql.type.AnimeKindEnum
import com.example.graphql.type.AnimeRatingEnum
import com.example.graphql.type.AnimeStatusEnum
import com.example.shikiflow.data.local.entity.PosterEntity
import com.example.shikiflow.data.local.entity.PosterEntity.Companion.toEntity
import com.example.shikiflow.data.local.entity.ReleaseDateEntity
import com.example.shikiflow.data.local.entity.ReleaseDateEntity.Companion.toEntity
import kotlin.time.Instant

@Entity(tableName = "anime_short")
data class AnimeShortEntity(
    @PrimaryKey val id: String,
    val name: String,
    val russian: String?,
    val japanese: String?,
    val kind: AnimeKindEnum?,
    val score: Double?,
    val status: AnimeStatusEnum?,
    val rating: AnimeRatingEnum?,
    val episodes: Int,
    val episodesAired: Int,
    val nextEpisodeAt: Instant?,
    val duration: Int?,
    @Embedded(prefix = "aired_on")
    val airedOn: ReleaseDateEntity?,
    @Embedded(prefix = "released_on")
    val releasedOn: ReleaseDateEntity?,
    @Embedded(prefix = "poster")
    val poster: PosterEntity?,
    val url: String,
) {
    companion object {
        fun AnimeShort.toEntity(): AnimeShortEntity {
            return AnimeShortEntity(
                id = this.id,
                name = this.name,
                russian = this.russian,
                japanese = this.japanese,
                kind = this.kind,
                score = this.score,
                status = this.status,
                rating = this.rating,
                episodes = this.episodes,
                episodesAired = this.episodesAired,
                nextEpisodeAt = this.nextEpisodeAt?.let { Instant.parse(it.toString()) },
                duration = this.duration,
                airedOn = this.airedOn?.toEntity(),
                releasedOn = this.releasedOn?.toEntity(),
                poster = this.poster?.toEntity(),
                url = this.url
            )
        }
    }
}
