package com.example.shikiflow.domain.model.track.media

import com.example.shikiflow.domain.model.media_details.Genre
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.media_details.MediaTitle
import com.example.shikiflow.domain.model.track.Date
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.track.Poster
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlin.time.Instant

data class MediaShortData(
    val id: Int,
    val malId: Int?,
    val title: MediaTitle,
    val synonyms: List<String>?,
    val mediaType: MediaType,
    val kind: MediaFormat?,
    val score: Float?,
    val status: MediaStatus?,
    val totalCount: Int?,
    val currentProgress: Int?,
    val volumes: Int?,
    val nextEpisodeAt: Instant?,
    val duration: Int?,
    val airedOn: Date?,
    val releasedOn: Date?,
    val poster: Poster?,
    val genres: List<Genre>,
    val studios: List<String>?
)
