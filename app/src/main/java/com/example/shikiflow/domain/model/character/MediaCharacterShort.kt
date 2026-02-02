package com.example.shikiflow.domain.model.character

import com.example.shikiflow.domain.model.media_details.MediaPersonShort

data class MediaCharacterShort(
    val mediaCharacter: MediaPersonShort,
    val role: CharacterRole,
    val mediaPerson: MediaPersonShort?
)
