package com.example.shikiflow.domain.model.media_details

import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.track.RelationKind
import com.example.shikiflow.domain.model.tracks.MediaType

data class RelatedMedia(
    val id: Int,
    val title: String,
    val coverImageUrl: String,
    val mediaFormat: MediaFormat,
    val relationKind: RelationKind,
    val mediaType: MediaType
)
