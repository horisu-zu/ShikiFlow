package com.example.shikiflow.domain.model.track.anime

import com.example.graphql.type.AnimeStatusEnum
import com.example.shikiflow.domain.model.track.anime.AnimeUserTrack
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserRateData
import kotlinx.datetime.toInstant

data class AnimeTrack(
    val track: AnimeUserTrack,
    val anime: AnimeShortData
) {
    companion object {
        fun AnimeTrack.toUserRateData() = UserRateData(
            id = track.id,
            mediaType = MediaType.ANIME,
            status = track.status.name,
            progress = track.episodes,
            rewatches = track.rewatches,
            score = track.score,
            mediaId = anime.id,
            title = anime.name,
            posterUrl = anime.poster?.previewUrl,
            createDate = track.createdAt.toString().toInstant(),
            updateDate = track.updatedAt.toString().toInstant(),
            totalEpisodes = if (anime.status == AnimeStatusEnum.released) anime.episodes
                else anime.episodesAired,
            totalChapters = null
        )
    }
}