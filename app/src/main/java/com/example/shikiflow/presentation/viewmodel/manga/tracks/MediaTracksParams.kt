package com.example.shikiflow.presentation.viewmodel.manga.tracks

import com.example.shikiflow.domain.model.tracks.RateUpdateState

data class MediaTracksParams(
    val userId: Int? = null,
    val rateUpdateState: RateUpdateState = RateUpdateState.INITIAL
)
