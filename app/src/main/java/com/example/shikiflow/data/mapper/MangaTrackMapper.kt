package com.example.shikiflow.data.mapper

import com.example.shikiflow.data.local.entity.mangatrack.MangaShortEntity
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrackDto
import com.example.shikiflow.data.local.entity.mangatrack.MangaTrackEntity
import com.example.shikiflow.data.mapper.AnimeTrackMapper.toDomain
import com.example.shikiflow.domain.model.track.manga.MangaShortData
import com.example.shikiflow.domain.model.track.manga.MangaTrack
import com.example.shikiflow.domain.model.track.manga.MangaUserTrack

object MangaTrackMapper {
    fun MangaTrackDto.toDomain(): MangaTrack {
        return MangaTrack(
            track = this.track.toDomain(),
            manga = this.manga.toDomain()
        )
    }

    fun MangaTrackEntity.toDomain(): MangaUserTrack {
        return MangaUserTrack(
            id = this.id,
            status = this.status,
            chapters = this.chapters,
            volumes = this.volumes,
            rewatches = this.rewatches,
            score = this.score,
            text = this.text,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            mangaId = this.mangaId
        )
    }

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
            poster = this.poster?.toDomain(),
            url = this.url,
        )
    }

    fun MangaUserTrack.toDto(): MangaTrackEntity {
        return MangaTrackEntity(
            id = this.id,
            status = this.status,
            chapters = this.chapters,
            volumes = this.volumes,
            rewatches = this.rewatches,
            score = this.score,
            text = this.text,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            mangaId = this.mangaId
        )
    }
}