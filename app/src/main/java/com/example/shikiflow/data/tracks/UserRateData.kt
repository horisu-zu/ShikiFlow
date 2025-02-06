package com.example.shikiflow.data.tracks

import com.example.graphql.AnimeDetailsQuery
import com.example.graphql.AnimeTracksQuery
import com.example.graphql.MangaDetailsQuery
import com.example.graphql.MangaTracksQuery
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant

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

fun AnimeTracksQuery.UserRate.toUiModel() = UserRateData(
    id = animeUserRateWithModel.id,
    mediaType = MediaType.ANIME,
    status = animeUserRateWithModel.status.rawValue,
    progress = animeUserRateWithModel.episodes,
    rewatches = animeUserRateWithModel.rewatches,
    score = animeUserRateWithModel.score,
    mediaId = animeUserRateWithModel.anime?.animeShort?.id.toString(),
    title = animeUserRateWithModel.anime?.animeShort?.name.toString(),
    posterUrl = animeUserRateWithModel.anime?.animeShort?.poster?.posterShort?.previewUrl,
    createDate = animeUserRateWithModel.createdAt.toString().toInstant(),
    updateDate = animeUserRateWithModel.updatedAt.toString().toInstant(),
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
    createDate = mangaUserRateWithModel.createdAt.toString().toInstant(),
    updateDate = mangaUserRateWithModel.updatedAt.toString().toInstant(),
    totalEpisodes = null,
    totalChapters = mangaUserRateWithModel.manga?.mangaShort?.chapters
)

fun AnimeDetailsQuery.UserRate.toUiModel(media: AnimeDetailsQuery.Anime?) = UserRateData(
    id = id,
    mediaType = MediaType.ANIME,
    status = status.rawValue,
    progress = episodes,
    rewatches = rewatches,
    score = score,
    mediaId = media?.id.toString(),
    title = media?.name.toString(),
    posterUrl = media?.poster?.originalUrl,
    createDate = createdAt.toString().toInstant(),
    updateDate = updatedAt.toString().toInstant(),
    totalEpisodes = media?.episodes,
    totalChapters = null
)

fun MangaDetailsQuery.UserRate.toUiModel(media: MangaDetailsQuery.Manga) = UserRateData(
    id = mangaUserRate.id,
    mediaType = MediaType.MANGA,
    status = mangaUserRate.status.rawValue,
    progress = mangaUserRate.chapters,
    rewatches = mangaUserRate.rewatches,
    score = mangaUserRate.score,
    mediaId = media.id,
    title = media.name,
    posterUrl = media.poster?.posterShort?.previewUrl,
    createDate = mangaUserRate.createdAt.toString().toInstant(),
    updateDate = mangaUserRate.updatedAt.toString().toInstant(),
    totalEpisodes = null,
    totalChapters = media.chapters
)
