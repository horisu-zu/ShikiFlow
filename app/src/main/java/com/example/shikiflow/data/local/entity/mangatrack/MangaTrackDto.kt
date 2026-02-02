package com.example.shikiflow.data.local.entity.mangatrack

import androidx.room.Embedded
import androidx.room.Relation
import com.example.shikiflow.data.local.entity.mangatrack.MangaShortEntity.Companion.toDomain
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrackEntity.Companion.toDomain
import com.example.shikiflow.domain.model.track.manga.MangaTrack

data class MangaTrackDto(
    @Embedded val track: MangaTrackEntity,
    @Relation(
        parentColumn = "mangaId",
        entityColumn = "id"
    ) val manga: MangaShortEntity
) {
    companion object {
        fun MangaTrackDto.toDomain(): MangaTrack {
            return MangaTrack(
                track = this.track.toDomain(),
                manga = this.manga.toDomain()
            )
        }
    }
}