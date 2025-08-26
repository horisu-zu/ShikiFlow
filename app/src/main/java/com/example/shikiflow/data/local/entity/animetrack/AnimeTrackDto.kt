package com.example.shikiflow.data.local.entity.animetrack

import androidx.room.Embedded
import androidx.room.Relation

data class AnimeTrackDto(
    @Embedded val track: AnimeTrackEntity,
    @Relation(
        parentColumn = "animeId",
        entityColumn = "id"
    )
    val anime: AnimeShortEntity
)