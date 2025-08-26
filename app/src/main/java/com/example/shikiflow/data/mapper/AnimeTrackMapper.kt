package com.example.shikiflow.data.mapper

import com.example.shikiflow.data.local.entity.animetrack.AnimeShortEntity
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackDto
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackEntity
import com.example.shikiflow.data.local.entity.PosterEntity
import com.example.shikiflow.data.local.entity.ReleaseDateEntity
import com.example.shikiflow.domain.model.track.anime.AnimeShortData
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.anime.AnimeUserTrack
import com.example.shikiflow.domain.model.track.Poster
import com.example.shikiflow.domain.model.track.ReleaseDate

object AnimeTrackMapper {
    fun AnimeTrackDto.toDomain(): AnimeTrack {
        return AnimeTrack(
            track = this.track.toDomain(),
            anime = this.anime.toDomain()
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

    fun AnimeShortEntity.toDomain(): AnimeShortData {
        return AnimeShortData(
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
            nextEpisodeAt = this.nextEpisodeAt,
            duration = this.duration,
            airedOn = this.airedOn?.toDomain(),
            releasedOn = this.releasedOn?.toDomain(),
            poster = this.poster?.toDomain(),
            url = this.url
        )
    }

    fun ReleaseDateEntity.toDomain(): ReleaseDate {
        return ReleaseDate(
            year = this.year,
            month = this.month,
            day = this.day,
            date = this.date
        )
    }

    fun PosterEntity.toDomain(): Poster {
        return Poster(
            originalUrl = this.originalUrl,
            mainUrl = this.mainUrl,
            previewUrl = this.previewUrl
        )
    }

    fun AnimeUserTrack.toDto(): AnimeTrackEntity {
        return AnimeTrackEntity(
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