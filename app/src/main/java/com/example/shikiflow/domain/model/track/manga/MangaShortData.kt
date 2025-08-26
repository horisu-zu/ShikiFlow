package com.example.shikiflow.domain.model.track.manga

import com.example.graphql.type.MangaKindEnum
import com.example.graphql.type.MangaStatusEnum
import com.example.shikiflow.domain.model.track.Poster
import com.example.shikiflow.domain.model.track.ReleaseDate

data class MangaShortData(
    val id: String,
    val name: String,
    val japanese: String?,
    val kind: MangaKindEnum?,
    val score: Double?,
    val status: MangaStatusEnum?,
    val chapters: Int,
    val volumes: Int,
    val airedOn: ReleaseDate?,
    val releasedOn: ReleaseDate?,
    val poster: Poster?,
    val url: String,
)
