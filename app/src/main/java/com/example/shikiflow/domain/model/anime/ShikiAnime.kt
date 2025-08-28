package com.example.shikiflow.domain.model.anime

import com.example.graphql.type.AnimeKindEnum
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.model.common.ShikiImage
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShikiAnime(
    val id: Int? = null,
    val name: String? = null,
    val russian: String? = null,
    val url: String? = null,
    val image: ShikiImage? = null,
    val kind: String? = null,
    val score: String? = null,
    val status: String? = null,
    val episodes: Int? = null,
    @SerialName("episodes_aired") val episodesAired: Int? = null,
    @SerialName("aired_on") val airedOn: String? = null,
    @SerialName("released_on") val releasedOn: String? = null,
    val roles: List<String>? = null,
    val role: String? = null
)

fun ShikiAnime.toBrowseAnime(): Browse.Anime {
    return Browse.Anime(
        id = this.id.toString(),
        title = this.name ?: "Unknown",
        posterUrl = "${BuildConfig.BASE_URL}${this.image?.original}",
        score = this.score?.toDouble() ?: 0.0,
        animeKind = AnimeKindEnum.valueOf(this.kind ?: "UNKNOWN__"),
        episodesAired = this.episodesAired ?: 0,
        episodes = this.episodes ?: 0,
        userRateStatus = null
    )
}
