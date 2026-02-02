package com.example.shikiflow.data.local.entity.animetrack

import androidx.room.Embedded
import androidx.room.Relation
import com.example.shikiflow.data.local.entity.animetrack.AnimeShortEntity.Companion.toDomain
import com.example.shikiflow.data.local.entity.animetrack.AnimeTrackEntity.Companion.toDomain
import com.example.shikiflow.domain.model.track.anime.AnimeTrack

data class AnimeTrackDto(
    @Embedded val track: AnimeTrackEntity,
    @Relation(
        parentColumn = "animeId",
        entityColumn = "id"
    )
    val anime: AnimeShortEntity
) {
    companion object {
        fun AnimeTrackDto.toDomain(): AnimeTrack {
            return AnimeTrack(
                track = this.track.toDomain(),
                anime = this.anime.toDomain()
            )
        }
    }
}