package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.fragment.MediaCoverShort
import com.example.graphql.shikimori.fragment.PosterShort
import com.example.shikiflow.domain.model.track.Poster

object PosterMapper {
    fun PosterShort.toDomain() = Poster(
        originalUrl = this.originalUrl,
        mainUrl = this.mainUrl,
        previewUrl = this.previewUrl
    )

    fun MediaCoverShort.toDomain() = Poster(
        originalUrl = this.extraLarge,
        mainUrl = this.large,
        previewUrl = this.medium
    )
}