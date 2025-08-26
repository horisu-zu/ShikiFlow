package com.example.shikiflow.data.local.entity

import com.example.graphql.fragment.AnimeShort.Poster
import com.example.graphql.fragment.MangaShort

data class PosterEntity(
    val originalUrl: String,
    val mainUrl: String,
    val previewUrl: String
) {
    companion object {
        fun Poster.toEntity(): PosterEntity {
            return PosterEntity(
                originalUrl = this.posterShort.originalUrl,
                mainUrl = this.posterShort.mainUrl,
                previewUrl = this.posterShort.previewUrl
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