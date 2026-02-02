package com.example.shikiflow.data.datasource.dto

import com.example.graphql.shikimori.type.AnimeKindEnum
import kotlinx.serialization.Serializable

@Serializable
data class ShikiShortAnime(
    val id: Long,
    val name: String,
    val kind: AnimeKindEnum?,
    val image: ShikiImage,
    val episodes: Int
)
