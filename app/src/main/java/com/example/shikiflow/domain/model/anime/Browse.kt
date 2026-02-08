package com.example.shikiflow.domain.model.anime

import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.search.BrowseOptions
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
sealed interface BrowseType {
    val displayValueRes: Int

    enum class AnimeBrowseType(override val displayValueRes: Int): BrowseType {
        ONGOING(R.string.browse_type_anime_ongoing),
        ANIME_TOP(R.string.browse_type_anime_top)
    }

    enum class MangaBrowseType(override val displayValueRes: Int): BrowseType {
        MANGA_TOP(R.string.browse_type_manga_top)
    }

    companion object {
        fun BrowseType.getBrowseOptions(): BrowseOptions {
            return when(this) {
                is AnimeBrowseType -> BrowseOptions(mediaType = MediaType.ANIME)
                is MangaBrowseType -> BrowseOptions(mediaType = MediaType.MANGA)
            }
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
