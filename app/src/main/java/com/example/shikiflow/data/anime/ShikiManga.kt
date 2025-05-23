package com.example.shikiflow.data.anime

import com.example.graphql.type.MangaKindEnum
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.common.ShikiImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShikiManga(
    val id: Int? = null,
    val name: String? = null,
    val russian: String? = null,
    val url: String? = null,
    val image: ShikiImage? = null,
    val kind: String? = null,
    val score: String? = null,
    val status: String? = null,
    val volumes: Int? = null,
    val chapters: Int? = null,
    @SerialName("aired_on") val airedOn: String? = null,
    @SerialName("released_on") val releasedOn: String? = null
)

fun ShikiManga.toBrowseManga(): Browse.Manga {
    return Browse.Manga(
        id = this.id.toString(),
        title = this.name ?: "Unknown",
        posterUrl = "${BuildConfig.BASE_URL}${this.image?.original}",
        score = this.score?.toDouble() ?: 0.0,
        mangaKind = MangaKindEnum.valueOf(this.kind ?: "UNKNOWN__"),
    )
}