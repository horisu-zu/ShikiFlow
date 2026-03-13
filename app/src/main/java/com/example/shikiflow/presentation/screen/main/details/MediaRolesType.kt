package com.example.shikiflow.presentation.screen.main.details

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.sort.CharacterType
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.domain.model.tracks.MediaType

enum class MediaRolesType {
    CHARACTER,
    STAFF
}

enum class RoleType(val displayValue: Int) {
    VA(R.string.role_voice_actor),
    ANIME(R.string.media_type_anime),
    MANGA(R.string.media_type_manga);

    companion object {
        fun RoleType.toMediaType(): MediaType {
            return when(this) {
                MANGA -> MediaType.MANGA
                else -> MediaType.ANIME
            }
        }
    }
}