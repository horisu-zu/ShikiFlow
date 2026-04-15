package com.example.shikiflow.data.mapper.local

import com.example.graphql.anilist.fragment.MediaListShort
import com.example.graphql.shikimori.fragment.AnimeUserRateWithModel
import com.example.graphql.shikimori.fragment.MangaUserRateWithModel
import com.example.shikiflow.data.local.entity.mediatrack.MediaTrackDto
import com.example.shikiflow.data.mapper.local.MediaShortMapper.toDomain
import com.example.shikiflow.data.mapper.local.MediaShortMapper.toShortData
import com.example.shikiflow.data.mapper.local.MediaTrackMapper.toDomain
import com.example.shikiflow.data.mapper.local.MediaTrackMapper.toTrack
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.track.media.MediaTrack
import com.example.shikiflow.domain.model.tracks.UserRateData

object TracksMapper {
    fun MediaTrackDto.toDomain(): MediaTrack {
        return MediaTrack(
            track = track.toDomain(),
            shortData = media.toDomain()
        )
    }

    fun AnimeUserRateWithModel.toDomain(): MediaTrack {
        return MediaTrack(
            track = toTrack(),
            shortData = anime?.animeShort?.toShortData()!!
        )
    }

    fun MangaUserRateWithModel.toDomain(): MediaTrack {
        return MediaTrack(
            track = toTrack(),
            shortData = manga?.mangaShort?.toShortData()!!
        )
    }

    fun MediaListShort.toDomain(): MediaTrack {
        return MediaTrack(
            track = toTrack(),
            shortData = this.media?.mediaShort?.toShortData()!!
        )
    }

    fun MediaTrack.toUserRateData() = UserRateData(
        id = track.id,
        mediaType = shortData.mediaType,
        status = track.status,
        progress = track.progress,
        progressVolumes = track.progressVolumes ?: 0,
        rewatches = track.repeat,
        score = track.score,
        mediaId = shortData.id,
        malId = shortData.malId,
        title = shortData.name,
        posterUrl = shortData.poster?.previewUrl,
        createDate = track.createdAt,
        updateDate = track.updatedAt,
        totalCount = if (shortData.status == MediaStatus.RELEASED) shortData.totalCount ?: 0
            else shortData.currentProgress ?: Int.MAX_VALUE,
        volumesCount = shortData.volumes ?: 0
    )
}