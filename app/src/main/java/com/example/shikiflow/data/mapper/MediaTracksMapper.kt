package com.example.shikiflow.data.mapper

import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.manga.MangaTrack
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserRateData

object MediaTracksMapper {
    fun AnimeTrack.toUserRateData() = UserRateData(
        id = track.id,
        mediaType = MediaType.ANIME,
        status = track.status,
        progress = track.episodes,
        progressVolumes = 0,
        rewatches = track.rewatches,
        score = track.score,
        mediaId = anime.id,
        title = anime.name,
        posterUrl = anime.poster?.previewUrl,
        createDate = track.createdAt,
        updateDate = track.updatedAt,
        totalCount = if (anime.status == MediaStatus.RELEASED) anime.episodes
            else anime.episodesAired,
        volumesCount = 0
    )

    fun MangaTrack.toUserRateData() = UserRateData(
        id = track.id,
        mediaType = MediaType.MANGA,
        status = track.status,
        progress = track.chapters,
        progressVolumes = track.volumes,
        rewatches = 0,
        score = track.score,
        mediaId = manga.id,
        title = manga.name,
        posterUrl = manga.poster?.previewUrl,
        createDate = track.createdAt,
        updateDate = track.updatedAt,
        totalCount = if(manga.status == MediaStatus.RELEASED) manga.chapters else Int.MAX_VALUE,
        volumesCount = if(manga.status == MediaStatus.RELEASED) manga.volumes else Int.MAX_VALUE
    )
}