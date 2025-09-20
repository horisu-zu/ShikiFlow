package com.example.shikiflow.domain.model.tracks

import com.example.graphql.AnimeDetailsQuery
import com.example.graphql.MangaDetailsQuery
import com.example.graphql.type.AnimeStatusEnum
import kotlin.time.Clock
import kotlin.time.Instant

enum class MediaType { ANIME, MANGA }

data class UserRateData(
    val id: String? = null,
    val mediaType: MediaType,
    val status: String,
    val progress: Int,
    val rewatches: Int,
    val score: Int,
    val mediaId: String,
    val title: String,
    val posterUrl: String?,
    val createDate: Instant,
    val updateDate: Instant,
    val totalEpisodes: Int?,
    val totalChapters: Int?
) {
    companion object {
        fun createEmpty(
            mediaId: String,
            mediaTitle: String,
            mediaPosterUrl: String?,
            mediaType: MediaType
        ) = UserRateData(
            id = null,
            mediaType = mediaType,
            status = "",
            progress = 0,
            rewatches = 0,
            score = 0,
            mediaId = mediaId,
            title = mediaTitle,
            posterUrl = mediaPosterUrl,
            createDate = Clock.System.now(),
            updateDate = Clock.System.now(),
            totalEpisodes = null,
            totalChapters = null
        )
    }
}

/*fun AnimeTracksQuery.UserRate.toUiModel() = UserRateData(
    id = animeUserRateWithModel.id,
    mediaType = MediaType.ANIME,
    status = animeUserRateWithModel.status.rawValue,
    progress = animeUserRateWithModel.episodes,
    rewatches = animeUserRateWithModel.rewatches,
    score = animeUserRateWithModel.score,
    mediaId = animeUserRateWithModel.anime?.animeShort?.id.toString(),
    title = animeUserRateWithModel.anime?.animeShort?.name.toString(),
    posterUrl = animeUserRateWithModel.anime?.animeShort?.poster?.posterShort?.previewUrl,
    createDate = Instant.parse(animeUserRateWithModel.createdAt.toString()),
    updateDate = Instant.parse(animeUserRateWithModel.updatedAt.toString()),
    totalEpisodes = animeUserRateWithModel.anime?.animeShort?.episodesAired,
    totalChapters = null
)

fun MangaTracksQuery.UserRate.toUiModel() = UserRateData(
    id = mangaUserRateWithModel.id,
    mediaType = MediaType.MANGA,
    status = mangaUserRateWithModel.status.rawValue,
    progress = mangaUserRateWithModel.chapters,
    rewatches = mangaUserRateWithModel.rewatches,
    score = mangaUserRateWithModel.score,
    mediaId = mangaUserRateWithModel.manga?.mangaShort?.id.toString(),
    title = mangaUserRateWithModel.manga?.mangaShort?.name.toString(),
    posterUrl = mangaUserRateWithModel.manga?.mangaShort?.poster?.posterShort?.previewUrl,
    createDate = Instant.parse(mangaUserRateWithModel.createdAt.toString()),
    updateDate = Instant.parse(mangaUserRateWithModel.updatedAt.toString()),
    totalEpisodes = null,
    totalChapters = mangaUserRateWithModel.manga?.mangaShort?.chapters
)*/

fun AnimeDetailsQuery.Anime.toUiModel(): UserRateData {
    return if(this.userRate != null) {
        UserRateData(
            id = userRate.id,
            mediaType = MediaType.ANIME,
            status = userRate.status.rawValue,
            progress = userRate.episodes,
            rewatches = userRate.rewatches,
            score = userRate.score,
            mediaId = id,
            title = name,
            posterUrl = poster?.originalUrl,
            createDate = Instant.parse(createdAt.toString()),
            updateDate = Instant.parse(updatedAt.toString()),
            totalEpisodes = if(status == AnimeStatusEnum.released) episodes else episodesAired,
            totalChapters = null
        )
    } else {
        UserRateData.createEmpty(
            mediaId = id,
            mediaTitle = name,
            mediaPosterUrl = poster?.originalUrl ?: "",
            mediaType = MediaType.ANIME
        )
    }
}

fun MangaDetailsQuery.Manga.toUiModel(): UserRateData {
    return if(userRate != null) {
        UserRateData(
            id = userRate.id,
            mediaType = MediaType.MANGA,
            status = userRate.status.rawValue,
            progress = userRate.chapters,
            rewatches = userRate.rewatches,
            score = userRate.score,
            mediaId = id,
            title = name,
            posterUrl = poster?.posterShort?.previewUrl,
            createDate = Instant.parse(userRate.createdAt.toString()),
            updateDate = Instant.parse(userRate.updatedAt.toString()),
            totalEpisodes = null,
            totalChapters = chapters
        )
    } else {
        UserRateData.createEmpty(
            mediaId = id,
            mediaTitle = name,
            mediaPosterUrl = poster?.posterShort?.originalUrl ?: "",
            mediaType = MediaType.MANGA
        )
    }
}
