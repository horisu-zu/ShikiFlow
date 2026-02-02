package com.example.shikiflow.domain.model.character

import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType

data class MediaRole(
    val id: Int,
    val mediaType: MediaType,
    val title: String,
    val coverImageUrl: String,
    val userRateStatus: UserRateStatus? = null
)
