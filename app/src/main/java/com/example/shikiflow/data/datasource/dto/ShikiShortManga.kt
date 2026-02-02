package com.example.shikiflow.data.datasource.dto

import com.example.graphql.shikimori.type.MangaKindEnum
import kotlinx.serialization.Serializable

@Serializable
data class ShikiShortManga(
    val id: Long,
    val name: String,
    val kind: MangaKindEnum?,
    val image: ShikiImage,
    val chapters: Int
)
