package com.example.shikiflow.domain.model.browse

import androidx.paging.PagingData
import androidx.paging.map
import com.example.shikiflow.domain.model.media_details.CountryOfOrigin
import com.example.shikiflow.domain.model.media_details.MediaPersonShort
import com.example.shikiflow.domain.model.media_details.MediaSeason
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.shikiflow.domain.model.user.User as DomainUser
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
sealed interface BrowseType {
    val mediaType: MediaType
    val sort: MediaSort
    val status: MediaStatus?
    val season: MediaSeason?
    val countryOfOrigin: CountryOfOrigin?

    enum class AnimeBrowseType: BrowseType {
        ANIME_TOP,
        ANIME_POPULARITY,
        TRENDING_NOW,
        POPULAR_THIS_SEASON,
        UPCOMING_NEXT_SEASON,
        NEWLY_ADDED,
        ONGOING;

        override val mediaType: MediaType
            get() = MediaType.ANIME

        override val sort: MediaSort
            get() = when(this) {
                ONGOING, ANIME_TOP -> MediaSort.Common.SCORE
                ANIME_POPULARITY,
                POPULAR_THIS_SEASON,
                UPCOMING_NEXT_SEASON-> MediaSort.Common.POPULARITY
                TRENDING_NOW -> MediaSort.Anilist.TRENDING
                NEWLY_ADDED -> MediaSort.Anilist.DATE_ADDED
            }

        //Added Announced for Newly Added titles cuz API isn't consistent and older titles may have higher ID
        override val status: MediaStatus?
            get() = when(this) {
                POPULAR_THIS_SEASON,
                ONGOING -> MediaStatus.ONGOING
                UPCOMING_NEXT_SEASON,
                NEWLY_ADDED -> MediaStatus.ANNOUNCED
                else -> null
            }

        override val season: MediaSeason?
            get() = when(this) {
                POPULAR_THIS_SEASON -> MediaSeason.currentSeason()
                UPCOMING_NEXT_SEASON -> MediaSeason.nextSeason()
                else -> null
            }

        override val countryOfOrigin: CountryOfOrigin?
            get() = null

        companion object {
            val navEntries: List<AnimeBrowseType> = listOf(
                ANIME_TOP,
                ANIME_POPULARITY,
                ONGOING
            )

            val alSections: List<AnimeBrowseType> = listOf(
                TRENDING_NOW,
                POPULAR_THIS_SEASON,
                UPCOMING_NEXT_SEASON,
                NEWLY_ADDED
            )
        }
    }

    enum class MangaBrowseType: BrowseType {
        MANGA_TOP,
        MANGA_POPULARITY,
        TRENDING_NOW,
        POPULAR_MANHWA,
        NEWLY_ADDED;

        override val mediaType: MediaType
            get() = MediaType.MANGA

        override val sort: MediaSort
            get() = when(this) {
                MANGA_TOP -> MediaSort.Common.SCORE
                MANGA_POPULARITY, POPULAR_MANHWA -> MediaSort.Common.POPULARITY
                TRENDING_NOW -> MediaSort.Anilist.TRENDING
                NEWLY_ADDED -> MediaSort.Anilist.DATE_ADDED
            }

        override val status: MediaStatus?
            get() = when(this) {
                NEWLY_ADDED -> MediaStatus.ANNOUNCED
                else -> null
            }

        override val season: MediaSeason?
            get() = null

        override val countryOfOrigin: CountryOfOrigin?
            get() = when(this) {
                POPULAR_MANHWA -> CountryOfOrigin.SOUTH_KOREA
                else -> null
            }

        companion object {
            val navEntries: List<MangaBrowseType> = listOf(
                MANGA_TOP,
                MANGA_POPULARITY
            )

            val alSections: List<MangaBrowseType> = listOf(
                TRENDING_NOW,
                POPULAR_MANHWA,
                NEWLY_ADDED
            )
        }
    }
}

sealed interface Browse {
    data class User(
        val data: DomainUser
    ): Browse

    data class Character(
        val data: MediaPersonShort
    ): Browse

    data class Staff(
        val data: MediaPersonShort
    ): Browse

    companion object {
        fun <T : Browse> Flow<PagingData<T>>.asBrowse(): Flow<PagingData<Browse>> =
            map { pagingData: PagingData<T> -> pagingData.map { it as Browse } }
    }
}

sealed interface BrowseMedia : Browse {
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
        val genres: List<String> = emptyList()
    ): BrowseMedia

    data class Manga(
        override val id: Int,
        override val title: String,
        override val posterUrl: String?,
        override val score: Float?,
        override val nextEpisodeAt: Instant? = null,
        override val mediaType: MediaType = MediaType.MANGA,
        override val mediaFormat: MediaFormat,
        override val userRateStatus: UserRateStatus?
    ): BrowseMedia
}