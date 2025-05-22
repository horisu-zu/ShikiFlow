package com.example.shikiflow.data.anime

import com.example.graphql.AnimeBrowseQuery
import com.example.graphql.MangaBrowseQuery
import com.example.graphql.type.AnimeKindEnum
import com.example.graphql.type.MangaKindEnum
import com.example.shikiflow.data.mapper.UserRateMapper
import com.example.shikiflow.data.tracks.MediaType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
sealed interface BrowseType {
    enum class AnimeBrowseType: BrowseType { ONGOING, SEARCH, ANIME_TOP }
    enum class MangaBrowseType: BrowseType { SEARCH, MANGA_TOP }
}

sealed interface Browse {
    val id: String
    val title: String
    val posterUrl: String?
    val score: Double
    val kind: String
    val nextEpisodeAt: Instant?
    val mediaType: MediaType

    data class Anime(
        override val id: String,
        override val title: String,
        override val posterUrl: String?,
        override val score: Double,
        override val nextEpisodeAt: Instant? = null,
        override val mediaType: MediaType = MediaType.ANIME,
        val animeKind: AnimeKindEnum,
    ): Browse {
        override val kind: String get() = UserRateMapper.mapAnimeKind(animeKind)
    }

    data class Manga(
        override val id: String,
        override val title: String,
        override val posterUrl: String?,
        override val score: Double,
        override val nextEpisodeAt: Instant? = null,
        override val mediaType: MediaType = MediaType.MANGA,
        val mangaKind: MangaKindEnum
    ): Browse {
        override val kind: String get() = UserRateMapper.mapMangaKind(mangaKind)
    }
}

fun AnimeBrowseQuery.Anime.toBrowseAnime(): Browse.Anime {
    return Browse.Anime(
        id = this.id,
        title = this.name,
        posterUrl = this.poster?.posterShort?.mainUrl,
        score = this.score ?: 0.0,
        animeKind = this.kind ?: AnimeKindEnum.UNKNOWN__,
        nextEpisodeAt = this.nextEpisodeAt?.let { Instant.parse(nextEpisodeAt.toString()) }
    )
}

fun MangaBrowseQuery.Manga.toBrowseManga(): Browse.Manga {
    return Browse.Manga(
        id = this.id,
        title = this.name,
        posterUrl = this.poster?.posterShort?.mainUrl,
        score = this.score ?: 0.0,
        mangaKind = this.kind ?: MangaKindEnum.UNKNOWN__
    )
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
