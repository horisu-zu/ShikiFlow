package com.example.shikiflow.domain.model.common

import com.example.shikiflow.domain.model.tracks.MediaType

interface RelatedInfo {
    val relationKind: Int
    val media: MediaBasicInfo?
}

data class PosterInfo(
    val mainUrl: String?
)

data class MediaRelatedInfo(
    override val relationKind: Int,
    override val media: MediaBasicInfo?,
): RelatedInfo

data class MediaBasicInfo(
    val id: String,
    val name: String,
    val kind: Int,
    val poster: PosterInfo?,
    val mediaType: MediaType
)
