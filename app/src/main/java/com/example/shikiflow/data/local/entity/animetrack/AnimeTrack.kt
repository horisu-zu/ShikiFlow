package com.example.shikiflow.data.local.entity.animetrack

import androidx.room.Embedded
import androidx.room.Relation
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.data.tracks.UserRateData
import kotlinx.datetime.toInstant

data class AnimeTrack(
    @Embedded val track: AnimeTrackEntity,
    @Relation(
        parentColumn = "animeId",
        entityColumn = "id"
    )
    val anime: AnimeShortEntity
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
            totalEpisodes = anime.episodesAired,
            totalChapters = null
        )
    }
}