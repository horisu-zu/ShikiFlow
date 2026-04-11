package com.example.shikiflow.presentation.viewmodel.anime.tracks.search

import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType

data class TracksParams(
    val userId: Int? = null,
    val query: String = "",
    val userRateStatus: UserRateStatus? = null,
    val mediaType: MediaType? = null
)