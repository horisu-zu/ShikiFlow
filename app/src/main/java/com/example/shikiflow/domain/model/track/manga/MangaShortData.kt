package com.example.shikiflow.domain.model.track.manga

import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.track.Poster
import com.example.shikiflow.domain.model.track.Date

data class MangaShortData(
    val id: Int,
    val name: String,
    val japanese: String?,
    val kind: MediaFormat?,
    val score: Float?,
    val status: MediaStatus?,
    val chapters: Int,
    val volumes: Int,
    val airedOn: Date?,
    val releasedOn: Date?,
    val poster: Poster?
)