package com.example.shikiflow.data.local.entity.animetrack

import com.example.graphql.fragment.AnimeShort.Poster

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
    }
}