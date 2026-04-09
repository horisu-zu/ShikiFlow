package com.example.shikiflow.data.datasource.dto.media

import com.example.graphql.shikimori.type.MangaKindEnum
import com.example.shikiflow.data.datasource.dto.ShikiImage
import kotlinx.serialization.Serializable

@Serializable
data class ShikiShortManga(
    override val id: Long,
    override val name: String,
    override val russian: String,
    override val image: ShikiImage,
    val kind: MangaKindEnum?,
    val chapters: Int
): ShikiShortMedia()
