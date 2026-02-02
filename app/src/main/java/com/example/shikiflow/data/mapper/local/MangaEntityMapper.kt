package com.example.shikiflow.data.mapper.local

import com.example.graphql.anilist.fragment.MediaListShort
import com.example.graphql.anilist.fragment.MediaShort
import com.example.graphql.shikimori.MangaBrowseQuery
import com.example.graphql.shikimori.fragment.MangaShort
import com.example.graphql.shikimori.fragment.MangaUserRate
import com.example.graphql.shikimori.fragment.MangaUserRateWithModel
import com.example.shikiflow.data.mapper.common.DateMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaStatusMapper.toDomain
import com.example.shikiflow.data.mapper.common.PosterMapper.toDomain
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toDomain
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.manga.MangaShortData
import com.example.shikiflow.domain.model.track.manga.MangaTrack
import com.example.shikiflow.domain.model.track.manga.MangaUserTrack
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import kotlin.time.Instant

object MangaEntityMapper {
    fun MangaUserRateWithModel.toDomain(): MangaTrack {
        return MangaTrack(
            track = this.toEntity(),
            manga = this.manga?.mangaShort?.toDomain()!!
        )
    }

    fun MediaListShort.toMangaDomain(): MangaTrack {
        return MangaTrack(
            track = this.toMangaEntity(),
            manga = this.media?.mediaShort?.toMangaEntity()!!
        )
    }

    fun MangaShort.toDomain(): MangaShortData {
        return MangaShortData(
            id = this.id.toInt(),
            name = this.name,
            japanese = this.japanese,
            kind = this.kind?.toDomain(),
            score = this.score?.toFloat(),
            status = this.status?.toDomain(),
            chapters = this.chapters,
            volumes = this.volumes,
            airedOn = this.airedOn?.dateShort?.toDomain(),
            releasedOn = this.releasedOn?.dateShort?.toDomain(),
            poster = this.poster?.posterShort?.toDomain()
        )
    }

    fun MediaShort.toMangaEntity() = MangaShortData(
        id = this.id,
        name = this.title?.romaji ?: title?.english ?: "No Title",
        japanese = this.title?.native,
        kind = this.format?.toDomain(),
        score = this.averageScore?.div(10.0f),
        status = this.status?.toDomain(),
        chapters = this.chapters ?: 0,
        volumes = this.volumes ?: 0,
        airedOn = null,
        releasedOn = null,
        poster = this.coverImage?.mediaCoverShort?.toDomain()
    )

    fun MangaUserRateWithModel.toEntity(): MangaUserTrack {
        return MangaUserTrack(
            id = this.id.toInt(),
            status = this.status.toDomain(),
            chapters = this.chapters,
            volumes = this.volumes,
            rewatches = this.rewatches,
            score = this.score,
            text = this.text,
            createdAt = Instant.parse(this.createdAt.toString()),
            updatedAt = Instant.parse(this.updatedAt.toString()),
            mangaId = this.manga?.mangaShort?.id?.toInt() ?: 0
        )
    }

    fun MangaUserRate.toEntity(): MangaUserTrack {
        return MangaUserTrack(
            id = this.id.toInt(),
            status = this.status.toDomain(),
            chapters = this.chapters,
            volumes = this.volumes,
            rewatches = this.rewatches,
            score = this.score,
            text = this.text,
            createdAt = Instant.parse(this.createdAt.toString()),
            updatedAt = Instant.parse(this.updatedAt.toString()),
            mangaId = this.manga?.id?.toInt() ?: 0
        )
    }

    fun MediaListShort.toMangaEntity(): MangaUserTrack {
        return MangaUserTrack(
            id = this.id,
            status = this.status?.toDomain() ?: UserRateStatus.UNKNOWN,
            chapters = this.progress ?: 0,
            volumes = this.progressVolumes ?: 0,
            rewatches = this.repeat ?: 0,
            score = this.score.score?.toInt() ?: 0,
            text = null,
            createdAt = Instant.fromEpochSeconds(this.createdAt?.toLong() ?: 0L),
            updatedAt = Instant.fromEpochSeconds(this.updatedAt?.toLong() ?: 0L),
            mangaId = this.media?.mediaShort?.id ?: 0
        )
    }

    fun UserMediaRate.toMangaEntity(): MangaUserTrack {
        return MangaUserTrack(
            id = this.rateId,
            status = this.rateStatus,
            chapters = this.progress,
            volumes = this.progressVolumes,
            rewatches = this.repeat,
            score = this.score,
            text = this.textNotes,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            mangaId = this.mediaId
        )
    }
}