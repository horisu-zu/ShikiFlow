package com.example.shikiflow.presentation.viewmodel.character.media

import com.example.shikiflow.domain.model.tracks.MediaType

data class MediaCharactersParams(
    val mediaId: Int? = null,
    val mediaType: MediaType? = null
)