package com.example.shikiflow.data.anime

import androidx.navigation.NavController
import com.example.graphql.AnimeBrowseQuery
import com.example.graphql.MangaBrowseQuery
import com.example.graphql.type.AnimeKindEnum
import com.example.graphql.type.MangaKindEnum
import com.example.shikiflow.data.mapper.UserRateMapper
import com.example.shikiflow.data.tracks.MediaType

sealed interface BrowseState {
    val mediaType: MediaType
    val isLoading: Boolean
    val hasMorePages: Boolean
    val currentPage: Int
    val error: String?

    data class AnimeBrowseState(
        val items: List<AnimeBrowseQuery.Anime> = emptyList(),
        override val mediaType: MediaType = MediaType.ANIME,
        override val isLoading: Boolean = false,
        override val hasMorePages: Boolean = true,
        override val currentPage: Int = 1,
        override val error: String? = null
    ) : BrowseState

    data class MangaBrowseState(
        val items: List<MangaBrowseQuery.Manga> = emptyList(),
        override val mediaType: MediaType = MediaType.MANGA,
        override val isLoading: Boolean = false,
        override val hasMorePages: Boolean = true,
        override val currentPage: Int = 1,
        override val error: String? = null
    ) : BrowseState
}

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

    data class Anime(
        override val id: String,
        override val title: String,
        override val posterUrl: String?,
        override val score: Double,
        val animeKind: AnimeKindEnum
    ) : Browse {
        override val kind: String get() = UserRateMapper.mapAnimeKind(animeKind)
    }

    data class Manga(
        override val id: String,
        override val title: String,
        override val posterUrl: String?,
        override val score: Double,
        val mangaKind: MangaKindEnum
    ) : Browse {
        override val kind: String get() = UserRateMapper.mapMangaKind(mangaKind)
    }
}

fun AnimeBrowseQuery.Anime.toBrowseAnime(): Browse.Anime {
    return Browse.Anime(
        id = this.id,
        title = this.name,
        posterUrl = this.poster?.posterShort?.mainUrl,
        score = this.score ?: 0.0,
        animeKind = this.kind ?: AnimeKindEnum.UNKNOWN__
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

sealed class BrowseScreens(val route: String) {
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
}
