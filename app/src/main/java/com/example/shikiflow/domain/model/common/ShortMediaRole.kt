package com.example.shikiflow.domain.model.common

import com.example.shikiflow.domain.model.character.CharacterRole
import com.example.shikiflow.domain.model.media_details.MediaPersonShort
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType

data class ShortMedia(
    val id: Int,
    val title: String,
    val mediaType: MediaType,
    val coverImageUrl: String,
    val userRateStatus: UserRateStatus?
)

sealed interface MediaRole

sealed interface SingleMediaRole : MediaRole {
    val shortMedia: ShortMedia
}

sealed interface MultiMediaRole : MediaRole {
    val shortMediaList: List<ShortMedia>
}

data class CharacterMediaRole(
    override val shortMedia: ShortMedia,
    val characterRole: CharacterRole? = null
): SingleMediaRole

data class StaffMediaRole(
    override val shortMedia: ShortMedia,
    val staffRoles: List<String>
): SingleMediaRole

data class VoiceActorMediaRole(
    val characterShort: MediaPersonShort,
    override val shortMediaList: List<ShortMedia>
): MultiMediaRole