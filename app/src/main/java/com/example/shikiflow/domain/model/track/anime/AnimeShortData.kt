package com.example.shikiflow.domain.model.track.anime

import com.example.graphql.type.AnimeKindEnum
import com.example.graphql.type.AnimeRatingEnum
import com.example.graphql.type.AnimeStatusEnum
import com.example.shikiflow.domain.model.track.Poster
import com.example.shikiflow.domain.model.track.ReleaseDate
import kotlin.time.Instant

data class AnimeShortData(
    val id: String,
    val name: String,
    val russian: String?,
    val japanese: String?,
    val kind: AnimeKindEnum?,
    val score: Double?,
    val status: AnimeStatusEnum?,
    val rating: AnimeRatingEnum?,
    val episodes: Int,
    val episodesAired: Int,
    val nextEpisodeAt: Instant?,
    val duration: Int?,
    val airedOn: ReleaseDate?,
    val releasedOn: ReleaseDate?,
    val poster: Poster?,
    val url: String,
)