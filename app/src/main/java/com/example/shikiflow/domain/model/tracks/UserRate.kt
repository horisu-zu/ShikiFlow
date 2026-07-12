package com.example.shikiflow.domain.model.tracks

import com.example.shikiflow.domain.model.media_details.Genre
import com.example.shikiflow.domain.model.media_details.MediaTagEnum
import com.example.shikiflow.domain.model.media_details.MediaTitle
import com.example.shikiflow.domain.model.track.UserRateStatus

data class UserRate(
    val id: Int,
    val status: UserRateStatus,
    val score: Int,
    val mediaType: MediaType
)

data class ShortUserMediaRate(
    val id: Int,
    val title: MediaTitle,
    val synonyms: List<String>,
    val imageUrl: String,
    val score: Int,
    val status: UserRateStatus,
    val progress: Int,
    val genres: List<Genre>,
    val tags: List<MediaTagEnum>
)