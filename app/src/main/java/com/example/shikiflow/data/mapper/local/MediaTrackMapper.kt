package com.example.shikiflow.data.mapper.local

import com.example.graphql.anilist.fragment.MediaListShort
import com.example.graphql.shikimori.fragment.AnimeUserRateWithModel
import com.example.graphql.shikimori.fragment.MangaUserRateWithModel
import com.example.shikiflow.data.local.entity.mediatrack.MediaTrackEntity
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toDomain
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.media.MediaUserTrack
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import kotlin.time.Instant

object MediaTrackMapper {
    fun MediaTrackEntity.toDomain(): MediaUserTrack {
        return MediaUserTrack(
            mediaId = mediaId,
            id = id,
            status = status,
            progress = progress,
            progressVolumes = progressVolumes,
            repeat = repeat,
            score = score,
            text = text,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun MediaUserTrack.toEntity(): MediaTrackEntity {
        return MediaTrackEntity(
            mediaId = mediaId,
            id = id,
            status = status,
            progress = progress,
            progressVolumes = progressVolumes,
            repeat = repeat,
            score = score,
            text = text,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun AnimeUserRateWithModel.toTrack(): MediaUserTrack {
        return MediaUserTrack(
            mediaId = anime?.animeShort?.id?.toInt() ?: 0,
            id = id.toInt(),
            status = status.toDomain(),
            progress = episodes,
            progressVolumes = null,
            repeat = rewatches,
            score = score,
            text = text,
            createdAt = Instant.parse(createdAt.toString()),
            updatedAt = Instant.parse(updatedAt.toString())
        )
    }

    fun MangaUserRateWithModel.toTrack(): MediaUserTrack {
        return MediaUserTrack(
            mediaId = manga?.mangaShort?.id?.toInt() ?: 0,
            id = id.toInt(),
            status = status.toDomain(),
            progress = chapters,
            progressVolumes = volumes,
            repeat = rewatches,
            score = score,
            text = text,
            createdAt = Instant.parse(createdAt.toString()),
            updatedAt = Instant.parse(updatedAt.toString())
        )
    }

    fun MediaListShort.toTrack(): MediaUserTrack {
        return MediaUserTrack(
            mediaId = media?.mediaShort?.id ?: 0,
            id = id,
            status = status?.toDomain() ?: UserRateStatus.UNKNOWN,
            progress = progress ?: 0,
            progressVolumes = progressVolumes,
            repeat = repeat ?: 0,
            score = score.score?.toInt() ?: 0,
            text = null,
            createdAt = Instant.fromEpochSeconds(createdAt?.toLong() ?: 0L),
            updatedAt = Instant.fromEpochSeconds(updatedAt?.toLong() ?: 0L)
        )
    }

    fun UserMediaRate.toMediaEntity(): MediaUserTrack {
        return MediaUserTrack(
            id = rateId,
            status = rateStatus,
            progress = progress,
            progressVolumes = progressVolumes,
            repeat = repeat,
            score = score,
            text = textNotes,
            createdAt = createdAt,
            updatedAt = updatedAt,
            mediaId = mediaId
        )
    }
}