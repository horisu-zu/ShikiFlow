package com.example.shikiflow.data.local.entity.mangatrack

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.graphql.fragment.MangaShort
import com.example.graphql.type.MangaKindEnum
import com.example.graphql.type.MangaStatusEnum
import com.example.shikiflow.data.local.entity.PosterEntity
import com.example.shikiflow.data.local.entity.PosterEntity.Companion.toEntity
import com.example.shikiflow.data.local.entity.ReleaseDateEntity
import com.example.shikiflow.data.local.entity.ReleaseDateEntity.Companion.toEntity

@Entity(tableName = "manga_short")
data class MangaShortEntity(
    @PrimaryKey val id: String,
    val name: String,
    val japanese: String?,
    val kind: MangaKindEnum?,
    val score: Double?,
    val status: MangaStatusEnum?,
    val chapters: Int,
    val volumes: Int,
    @Embedded(prefix = "aired_on")
    val airedOn: ReleaseDateEntity?,
    @Embedded(prefix = "released_on")
    val releasedOn: ReleaseDateEntity?,
    @Embedded(prefix = "poster")
    val poster: PosterEntity?,
    val url: String,
) {
    companion object {
        fun MangaShort.toEntity(): MangaShortEntity {
            return MangaShortEntity(
                id = this.id,
                name = this.name,
                japanese = this.japanese,
                kind = this.kind,
                score = this.score,
                status = this.status,
                chapters = this.chapters,
                volumes = this.volumes,
                airedOn = this.airedOn?.toEntity(),
                releasedOn = this.releasedOn?.toEntity(),
                poster = this.poster?.toEntity(),
                url = this.url
            )
        }
    }
}
