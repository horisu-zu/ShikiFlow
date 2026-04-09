package com.example.shikiflow.data.datasource.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CalendarAnime(
    @SerialName("next_episode") val nextEpisode: Int,
    @SerialName("next_episode_at") val nextEpisodeAt: String,
    val duration: Int? = null,
    @SerialName("anime") val shikiAnime: ShikiAnime
)
