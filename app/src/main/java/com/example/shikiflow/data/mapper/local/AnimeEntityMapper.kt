package com.example.shikiflow.data.mapper.local

import com.example.graphql.anilist.fragment.MediaListShort
import com.example.graphql.anilist.fragment.MediaShort
import com.example.graphql.shikimori.fragment.AnimeShort
import com.example.graphql.shikimori.fragment.AnimeUserRate
import com.example.graphql.shikimori.fragment.AnimeUserRateWithModel
import com.example.shikiflow.data.local.entity.animetrack.AnimeShortEntity.Companion.toDomain
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackDto
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackEntity.Companion.toDomain
import com.example.shikiflow.data.mapper.common.DateMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaStatusMapper.toDomain
import com.example.shikiflow.data.mapper.common.PosterMapper.toDomain
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toDomain
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.anime.AnimeShortData
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.anime.AnimeUserTrack
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import kotlin.time.Instant

object AnimeEntityMapper {
    fun AnimeTrackDto.toDomain(): AnimeTrack {
        return AnimeTrack(
            track = this.track.toDomain(),
            anime = this.anime.toDomain()
        )
    }

    fun AnimeUserRateWithModel.toDomain(): AnimeTrack {
        return AnimeTrack(
            track = this.toEntity(),
            anime = this.anime?.animeShort?.toDomain()!!
        )
    }

    fun MediaListShort.toAnimeDomain(): AnimeTrack {
        return AnimeTrack(
            track = this.toAnimeEntity(),
            anime = this.media?.mediaShort?.toAnimeEntity()!!
        )
    }

    fun UserMediaRate.toAnimeEntity(): AnimeUserTrack {
        return AnimeUserTrack(
            id = this.rateId,
            status = this.rateStatus,
            episodes = this.progress,
            rewatches = this.repeat,
            score = this.score,
            text = this.textNotes,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            animeId = this.mediaId
        )
    }

    fun AnimeUserRateWithModel.toEntity(): AnimeUserTrack {
        return AnimeUserTrack(
            id = this.id.toInt(),
            status = this.status.toDomain(),
            episodes = this.episodes,
            rewatches = this.rewatches,
            score = this.score,
            text = this.text,
            createdAt = Instant.parse(this.createdAt.toString()),
            updatedAt = Instant.parse(this.updatedAt.toString()),
            animeId = this.anime?.animeShort?.id?.toInt() ?: 0
        )
    }

    fun AnimeUserRate.toEntity(): AnimeUserTrack {
        return AnimeUserTrack(
            id = this.id.toInt(),
            status = this.status.toDomain(),
            episodes = this.episodes,
            rewatches = this.rewatches,
            score = this.score,
            text = this.text,
            createdAt = Instant.parse(this.createdAt.toString()),
            updatedAt = Instant.parse(this.updatedAt.toString()),
            animeId = this.anime?.id?.toInt() ?: 0
        )
    }

    fun MediaListShort.toAnimeEntity(): AnimeUserTrack {
        return AnimeUserTrack(
            id = this.id,
            status = this.status?.toDomain() ?: UserRateStatus.UNKNOWN,
            episodes = this.progress ?: 0,
            rewatches = this.repeat ?: 0,
            score = this.score.score?.toInt() ?: 0,
            text = null,
            createdAt = Instant.fromEpochSeconds(this.createdAt?.toLong() ?: 0L),
            updatedAt = Instant.fromEpochSeconds(this.updatedAt?.toLong() ?: 0L),
            animeId = this.media?.mediaShort?.id ?: 0
        )
    }

    fun AnimeShort.toDomain() = AnimeShortData(
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
        airedOn = this.airedOn?.dateShort?.toDomain(),
        releasedOn = this.releasedOn?.dateShort?.toDomain(),
        studios = this.studios.map { name },
        genres = this.genres?.map { name } ?: emptyList(),
        poster = this.poster?.posterShort?.toDomain()
    )

    fun MediaShort.toAnimeEntity() = AnimeShortData(
        id = this.id,
        name = this.title?.romaji ?: "No Title",
        japanese = null,
        kind = this.format?.toDomain(),
        score = this.averageScore?.div(10.0f),
        status = this.status?.toDomain(),
        episodes = this.episodes ?: 0,
        episodesAired = this.nextAiringEpisode?.episode?.minus(1) ?: 0,
        nextEpisodeAt =  this.nextAiringEpisode?.let { Instant.fromEpochMilliseconds(it.airingAt.toLong()) },
        duration = this.duration,
        airedOn = this.startDate?.date?.toDomain(),
        releasedOn = this.endDate?.date?.toDomain(),
        studios = this.studios?.nodes?.mapNotNull { it?.name } ?: emptyList(),
        genres = this.genres?.mapNotNull { it } ?: emptyList(),
        poster = this.coverImage?.mediaCoverShort?.toDomain()
    )
}