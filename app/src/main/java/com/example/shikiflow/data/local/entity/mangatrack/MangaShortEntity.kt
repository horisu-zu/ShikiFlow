package com.example.shikiflow.data.local.entity.mangatrack

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shikiflow.data.local.entity.PosterEntity
import com.example.shikiflow.data.local.entity.PosterEntity.Companion.toDomain
import com.example.shikiflow.data.local.entity.PosterEntity.Companion.toDto
import com.example.shikiflow.data.local.entity.ReleaseDateEntity
import com.example.shikiflow.data.local.entity.ReleaseDateEntity.Companion.toDomain
import com.example.shikiflow.data.local.entity.ReleaseDateEntity.Companion.toDto
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.track.manga.MangaShortData

@Entity(tableName = "manga_short")
data class MangaShortEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val japanese: String?,
    val kind: MediaFormat?,
    val score: Float?,
    val status: MediaStatus?,
    val chapters: Int,
    val volumes: Int,
    @Embedded(prefix = "aired_on")
    val airedOn: ReleaseDateEntity?,
    @Embedded(prefix = "released_on")
    val releasedOn: ReleaseDateEntity?,
    @Embedded(prefix = "poster")
    val poster: PosterEntity?
) {
    companion object {
        fun MangaShortEntity.toDomain(): MangaShortData {
            return MangaShortData(
                id = this.id,
                name = this.name,
                japanese = this.japanese,
                kind = this.kind,
                score = this.score,
                status = this.status,
                chapters = this.chapters,
                volumes = this.volumes,
                airedOn = this.airedOn?.toDomain(),
                releasedOn = this.releasedOn?.toDomain(),
                poster = this.poster?.toDomain()
            )
        }

        fun MangaShortData.toDto(): MangaShortEntity {
            return MangaShortEntity(
                id = this.id,
                name = this.name,
                japanese = this.japanese,
                kind = this.kind,
                score = this.score,
                status = this.status,
                chapters = this.chapters,
                volumes = this.volumes,
                airedOn = this.airedOn?.toDto(),
                releasedOn = this.releasedOn?.toDto(),
                poster = this.poster?.toDto()
            )
        }
    }
}
