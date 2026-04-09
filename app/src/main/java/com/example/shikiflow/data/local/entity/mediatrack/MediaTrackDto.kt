package com.example.shikiflow.data.local.entity.mediatrack

import androidx.room.Embedded
import androidx.room.Relation

data class MediaTrackDto(
    @Embedded val track: MediaTrackEntity,
    @Relation(
        parentColumn = "mediaId",
        entityColumn = "id"
    )
    val media: MediaShortEntity
)
