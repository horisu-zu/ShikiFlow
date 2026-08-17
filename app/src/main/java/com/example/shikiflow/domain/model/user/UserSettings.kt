package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.tracks.MediaType

data class UserSettings(
    val showAdultContent: Boolean = false,
    val preferredTitleType: PreferredTitleType = PreferredTitleType.ROMAJI,
    val scoreFormat: ScoreFormat = ScoreFormat.POINT_10,
    val customLists: Map<MediaType, List<String>> = emptyMap()
)
