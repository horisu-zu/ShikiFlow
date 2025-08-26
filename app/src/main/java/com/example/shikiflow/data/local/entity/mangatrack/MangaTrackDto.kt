package com.example.shikiflow.data.local.entity.mangatrack

import androidx.room.Embedded
import androidx.room.Relation

data class MangaTrackDto(
    @Embedded val track: MangaTrackEntity,
    @Relation(
        parentColumn = "mangaId",
        entityColumn = "id"
    ) val manga: MangaShortEntity
)
