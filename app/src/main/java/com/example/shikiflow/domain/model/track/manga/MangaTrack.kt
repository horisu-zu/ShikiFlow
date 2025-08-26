package com.example.shikiflow.domain.model.track.manga

import com.example.graphql.type.MangaKindEnum
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserRateData
import kotlinx.datetime.toInstant

data class MangaTrack(
    val track: MangaUserTrack,
    val manga: MangaShortData
) {
    companion object {
        fun MangaTrack.toUserRateData() = UserRateData(
            id = track.id,
            mediaType = MediaType.MANGA,
            status = track.status.name,
            progress = track.chapters,
            rewatches = 0,
            score = track.score,
            mediaId = manga.id,
            title = manga.name,
            posterUrl = manga.poster?.previewUrl,
            createDate = track.createdAt.toString().toInstant(),
            updateDate = track.updatedAt.toString().toInstant(),
            totalEpisodes = null,
            totalChapters = manga.chapters
        )

        fun MangaTrack.toBrowse() = Browse.Manga(
            id = this.manga.id,
            title = this.manga.name,
            posterUrl = this.manga.poster?.mainUrl,
            score = this.manga.score ?: 0.0,
            mangaKind = this.manga.kind ?: MangaKindEnum.UNKNOWN__
        )
    }
}
