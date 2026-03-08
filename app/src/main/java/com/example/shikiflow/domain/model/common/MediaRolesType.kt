package com.example.shikiflow.domain.model.common

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType

enum class MediaRolesType {
    CHARACTER,
    STAFF
}

enum class RoleType(val displayValue: Int) {
    VA(R.string.role_voice_actor),
    ANIME(R.string.browse_search_media_anime),
    MANGA(R.string.browse_search_media_manga);

    companion object {
        fun RoleType.toMediaType(): MediaType {
            return when(this) {
                MANGA -> MediaType.MANGA
                else -> MediaType.ANIME
            }
        }
    }
}