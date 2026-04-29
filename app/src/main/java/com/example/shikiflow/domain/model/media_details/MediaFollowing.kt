package com.example.shikiflow.domain.model.media_details

import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User

data class MediaFollowing(
    val user: User,
    val mediaType: MediaType,
    val status: UserRateStatus,
    val progress: Int?,
    val progressVolumes: Int?,
    val score: Float?,
    val scoreFormat: ScoreFormat
)
