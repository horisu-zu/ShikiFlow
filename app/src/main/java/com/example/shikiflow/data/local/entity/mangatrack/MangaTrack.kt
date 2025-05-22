package com.example.shikiflow.data.local.entity.mangatrack

import androidx.room.Embedded
import androidx.room.Relation
import com.example.graphql.type.MangaKindEnum
import com.example.shikiflow.data.anime.Browse
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.data.tracks.UserRateData
import kotlinx.datetime.toInstant

data class MangaTrack(
    @Embedded val track: MangaTrackEntity,
    @Relation(
        parentColumn = "mangaId",
        entityColumn = "id"
    ) val manga: MangaShortEntity
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
