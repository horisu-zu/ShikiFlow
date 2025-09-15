package com.example.shikiflow.domain.model.anime

import com.example.graphql.type.AnimeKindEnum
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.model.common.ShikiImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SimilarAnime(
    val id: Long,
    val name: String,
    val image: ShikiImage,
    val url: String? = null,
    val kind: String? = null,
    val score: Double? = null,
    val status: String? = null,
    val episodes: Int,
    @SerialName("episodes_aired") val episodesAired: Int? = null,
    @SerialName("aired_on") val airedOn: String? = null,
    @SerialName("released_on") val releasedOn: String? = null,
)


fun SimilarAnime.toBrowseAnime(): Browse.Anime {
    return Browse.Anime(
        id = this.id.toString(),
        title = this.name,
        posterUrl = "${BuildConfig.BASE_URL}${this.image.original}",
        score = this.score ?: 0.0,
        animeKind = AnimeKindEnum.valueOf(this.kind ?: "UNKNOWN__"),
        episodesAired = this.episodesAired ?: 0,
        episodes = this.episodes,
    )
}
