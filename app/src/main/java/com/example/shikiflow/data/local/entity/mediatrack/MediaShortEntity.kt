package com.example.shikiflow.data.local.entity.mediatrack

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shikiflow.data.local.entity.PosterEntity
import com.example.shikiflow.data.local.entity.ReleaseDateEntity
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlin.time.Instant

@Entity(tableName = "media_short")
data class MediaShortEntity(
    @PrimaryKey val id: Int,
    val malId: Int?,
    val name: String,
    val synonyms: List<String>?,
    val mediaType: MediaType,
    val kind: MediaFormat?,
    val score: Float?,
    val status: MediaStatus?,
    val progress: Int?,
    val episodesAired: Int?,
    val volumes: Int?,
    val nextEpisodeAt: Instant?,
    val duration: Int?,
    @Embedded(prefix = "aired_on")
    val airedOn: ReleaseDateEntity?,
    @Embedded(prefix = "released_on")
    val releasedOn: ReleaseDateEntity?,
    @Embedded(prefix = "poster")
    val poster: PosterEntity?,
    val genres: List<String>,
    val studios: List<String>?
)
