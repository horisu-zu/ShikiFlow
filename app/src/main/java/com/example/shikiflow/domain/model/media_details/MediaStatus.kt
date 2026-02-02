package com.example.shikiflow.domain.model.media_details

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.tracks.MediaType

enum class MediaStatus(
    val displayValue: Int,
    val mediaType: Set<MediaType> = setOf(MediaType.ANIME, MediaType.MANGA),
    val exclusions: Set<Pair<AuthType, MediaType>> = emptySet()
) {
    ANNOUNCED(R.string.media_status_announced),
    ONGOING(R.string.media_status_ongoing),
    RELEASED(R.string.media_status_released),
    CANCELLED(
        displayValue = R.string.media_status_manga_cancelled,
        exclusions = setOf(AuthType.SHIKIMORI to MediaType.ANIME)
    ),
    HIATUS(R.string.media_status_manga_hiatus, setOf(MediaType.MANGA)),
    UNKNOWN(R.string.common_unknown, emptySet());
}