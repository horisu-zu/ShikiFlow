package com.example.shikiflow.domain.model.anime

import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
sealed interface BrowseType {
    val mediaType: MediaType
    val sort: MediaSort

    enum class AnimeBrowseType: BrowseType {
        ONGOING,
        ANIME_TOP,
        ANIME_POPULARITY;

        override val mediaType: MediaType
            get() = MediaType.ANIME

        override val sort: MediaSort
            get() = when(this) {
                ONGOING, ANIME_TOP-> MediaSort.Common.SCORE
                ANIME_POPULARITY -> MediaSort.Common.POPULARITY
            }
    }

    enum class MangaBrowseType: BrowseType {
        MANGA_TOP,
        MANGA_POPULARITY;

        override val mediaType: MediaType
            get() = MediaType.MANGA

        override val sort: MediaSort
            get() = when(this) {
                MANGA_TOP-> MediaSort.Common.SCORE
                MANGA_POPULARITY -> MediaSort.Common.POPULARITY
            }
    }
}

sealed interface Browse {
    val id: Int
    val title: String
    val posterUrl: String?
    val score: Float?
    val nextEpisodeAt: Instant?
    val mediaType: MediaType
    val mediaFormat: MediaFormat?
    val userRateStatus: UserRateStatus?

    data class Anime(
        override val id: Int,
        override val title: String,
        override val posterUrl: String?,
        override val score: Float?,
        override val nextEpisodeAt: Instant? = null,
        override val mediaType: MediaType = MediaType.ANIME,
        override val mediaFormat: MediaFormat,
        override val userRateStatus: UserRateStatus?,
        val episodesAired: Int?,
        val episodes: Int?,
        val studios: List<String> = emptyList(),
        val genres: List<String> = emptyList(),

    ): Browse

    data class Manga(
        override val id: Int,
        override val title: String,
        override val posterUrl: String?,
        override val score: Float?,
        override val nextEpisodeAt: Instant? = null,
        override val mediaType: MediaType = MediaType.MANGA,
        override val mediaFormat: MediaFormat,
        override val userRateStatus: UserRateStatus?
    ): Browse
}

/*sealed class BrowseScreens(val route: String) {
    data object SideScreen : BrowseScreens("sideScreen/{browseType}") {
        const val ARG_BROWSE_TYPE = "browseType"

        private val browseTypeMap = buildMap<String, BrowseType> {
            BrowseType.AnimeBrowseType.entries.forEach { type ->
                put(type.name, type)
            }
            BrowseType.MangaBrowseType.entries.forEach { type ->
                put(type.name, type)
            }
        }

        fun createRoute(browseType: BrowseType) = when(browseType) {
            is BrowseType.AnimeBrowseType -> "sideScreen/${browseType.name}"
            is BrowseType.MangaBrowseType -> "sideScreen/${browseType.name}"
        }

        fun parseBrowseType(value: String): BrowseType? = browseTypeMap[value]
    }
}

fun NavController.navigateToSideScreen(browseType: BrowseType) {
    navigate(BrowseScreens.SideScreen.createRoute(browseType))
}*/
