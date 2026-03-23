package com.example.shikiflow.presentation.viewmodel.anime.tracks.search

import com.example.shikiflow.domain.model.track.UserRateStatus

data class TracksParams(
    val userId: String? = null,
    val query: String = "",
    val userRateStatus: UserRateStatus? = null
)