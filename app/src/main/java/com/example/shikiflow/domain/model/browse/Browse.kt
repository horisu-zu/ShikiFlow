package com.example.shikiflow.domain.model.browse

import androidx.paging.PagingData
import androidx.paging.map
import com.example.shikiflow.domain.model.media_details.MediaPersonShort
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

    enum class AnimeBrowseType: BrowseType {
        ANIME_TOP,
        ANIME_POPULARITY,
        ONGOING;

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