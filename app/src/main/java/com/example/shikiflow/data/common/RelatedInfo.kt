package com.example.shikiflow.data.common

interface RelatedInfo {
    val relationKind: String
    val anime: AnimeBasicInfo?
    val manga: MangaBasicInfo?
}

interface BasicInfo {
    val name: String
    val kind: String?
    val poster: PosterInfo?
}

data class PosterInfo(
    val mainUrl: String?
)

data class AnimeRelatedInfo(
    override val relationKind: String,
    override val anime: AnimeBasicInfo?,
    override val manga: MangaBasicInfo?
) : RelatedInfo

data class AnimeBasicInfo(
    override val name: String,
    override val kind: String?,
    override val poster: PosterInfo?
) : BasicInfo

data class MangaRelatedInfo(
    override val relationKind: String,
    override val anime: AnimeBasicInfo?,
    override val manga: MangaBasicInfo?
) : RelatedInfo

data class MangaBasicInfo(
    override val name: String,
    override val kind: String?,
    override val poster: PosterInfo?
) : BasicInfo