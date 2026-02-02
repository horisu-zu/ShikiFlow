package com.example.shikiflow.domain.model.track.anime

import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.track.Poster
import com.example.shikiflow.domain.model.track.Date
import com.example.shikiflow.domain.model.track.manga.MangaShortData
import kotlin.time.Instant

data class AnimeShortData(
    val id: Int,
    val name: String,
    val japanese: String?,
    val kind: MediaFormat?,
    val score: Float?,
    val status: MediaStatus?,
    val episodes: Int,
    val episodesAired: Int,
    val nextEpisodeAt: Instant?,
    val duration: Int?,
    val airedOn: Date?,
    val releasedOn: Date?,
    val studios: List<String>,
    val genres: List<String>,
    val poster: Poster?
) {
    companion object {
        fun MediaDetails.toShortAnimeData(): AnimeShortData {
            return AnimeShortData(
                id = id,
                name = title,
                japanese = native,
                kind = format,
                score = score,
                status = status,
                episodes = totalCount ?: 0,
                episodesAired = currentProgress ?: 0,
                nextEpisodeAt = nextEpisodeAt,
                duration = durationMins,
                airedOn = airedOn,
                releasedOn = releasedOn,
                studios = studios?.map { it.name } ?: emptyList(),
                genres = genres,
                poster = Poster(originalUrl = coverImageUrl)
            )
        }

        fun MediaDetails.toShortMangaData(): MangaShortData {
            return MangaShortData(
                id = id,
                name = title,
                japanese = native,
                kind = format,
                score = score,
                status = status,
                chapters = totalCount ?: 0,
                volumes = volumes ?: 0,
                airedOn = airedOn,
                releasedOn = releasedOn,
                poster = Poster(mainUrl = coverImageUrl)
            )
        }
    }
}