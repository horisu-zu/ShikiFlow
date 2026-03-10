package com.example.shikiflow.domain.model.search

import com.example.shikiflow.domain.model.media_details.AgeRating
import com.example.shikiflow.domain.model.media_details.MediaSeason
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.sort.BrowseOrder
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.tracks.MediaType

data class BrowseOptions(
    val mediaType: MediaType,
    val name: String? = null,
    val status: MediaStatus? = null,
    val order: BrowseOrder? = null,
    val format: MediaFormat? = null,
    val season: MediaSeason? = null,
    val genre: String? = null,
    val score: Int? = null,
    val ageRating: AgeRating? = null
)