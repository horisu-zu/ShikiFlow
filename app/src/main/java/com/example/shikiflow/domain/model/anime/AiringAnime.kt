package com.example.shikiflow.domain.model.anime

import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlin.time.Duration
import kotlin.time.Instant

data class AiringAnime(
    val data: AiringAnimeDataShort,
    val episode: Int,
    val timeUntilAiring: Duration?,
    val airingAt: Instant?,
    val releasedOn: Instant? = null
)

data class AiringAnimeDataShort(
    val id: Int,
    val title: String,
    val mediaType: MediaType,
    val coverImageUrl: String,
    val totalEpisodes: Int?,
    val userRateStatus: UserRateStatus?,
    val duration: Duration?,
    val isAdult: Boolean = false
)
