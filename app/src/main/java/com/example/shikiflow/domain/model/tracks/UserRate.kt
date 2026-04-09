package com.example.shikiflow.domain.model.tracks

import com.example.shikiflow.domain.model.track.UserRateStatus

data class UserRate(
    val id: Int,
    val status: UserRateStatus,
    val score: Int,
    val mediaType: MediaType
)

data class ShortUserMediaRate(
    val id: Int,
    val title: String,
    val enTitle: String? = null,
    val ruTitle: String? = null,
    val imageUrl: String,
    val score: Int,
    val status: UserRateStatus,
    val progress: Int
)