package com.example.shikiflow.data.local.entity

import com.example.graphql.shikimori.fragment.AnimeShort.Poster
import com.example.graphql.shikimori.fragment.MangaShort
import com.example.shikiflow.domain.model.track.Poster as PosterDomain

data class PosterEntity(
    val originalUrl: String?,
    val mainUrl: String?,
    val previewUrl: String? = null
) {
    companion object {
        fun Poster.toEntity(): PosterEntity {
            return PosterEntity(
                originalUrl = this.posterShort.originalUrl,
                mainUrl = this.posterShort.mainUrl,
                previewUrl = this.posterShort.previewUrl
            )
        }

        fun PosterDomain.toDto(): PosterEntity {
            return PosterEntity(
                originalUrl = this.originalUrl,
                mainUrl = this.mainUrl,
                previewUrl = this.previewUrl
            )
        }

        fun PosterEntity.toDomain(): PosterDomain {
            return PosterDomain(
                originalUrl = this.originalUrl,
                mainUrl = this.mainUrl,
                previewUrl = this.previewUrl
            )
        }

        fun MangaShort.Poster.toEntity(): PosterEntity {
            return PosterEntity(
                originalUrl = this.posterShort.originalUrl,
                mainUrl = this.posterShort.mainUrl,
                previewUrl = this.posterShort.previewUrl
            )
        }
    }
}