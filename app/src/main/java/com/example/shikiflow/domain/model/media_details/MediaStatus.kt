package com.example.shikiflow.domain.model.media_details

import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.tracks.MediaType

enum class MediaStatus(
    val mediaType: Set<MediaType> = setOf(MediaType.ANIME, MediaType.MANGA),
    val exclusions: Set<Pair<AuthType, MediaType>> = emptySet()
) {
    ANNOUNCED,
    ONGOING,
    RELEASED,
    CANCELLED(exclusions = setOf(AuthType.SHIKIMORI to MediaType.ANIME)),
    HIATUS(setOf(MediaType.MANGA)),
    UNKNOWN(emptySet());
}