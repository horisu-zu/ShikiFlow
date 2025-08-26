package com.example.shikiflow.domain.model.common

import com.example.shikiflow.domain.model.tracks.MediaType

interface RelatedInfo {
    val relationKind: String
    val media: MediaBasicInfo?
}

interface BasicInfo {
    val id: String
    val name: String
    val kind: String?
    val poster: PosterInfo?
    val mediaType: MediaType
}

data class PosterInfo(
    val mainUrl: String?
)

data class MediaRelatedInfo(
    override val relationKind: String,
    override val media: MediaBasicInfo?,
): RelatedInfo

data class MediaBasicInfo(
    override val id: String,
    override val name: String,
    override val kind: String?,
    override val poster: PosterInfo?,
    override val mediaType: MediaType
): BasicInfo
