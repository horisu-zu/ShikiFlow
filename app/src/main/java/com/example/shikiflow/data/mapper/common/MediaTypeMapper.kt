package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.type.MediaType as AnilistType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.MediaType.ANIME
import com.example.shikiflow.domain.model.tracks.MediaType.MANGA

object MediaTypeMapper {
    fun MediaType.toAnilistType(): AnilistType {
        return when(this) {
            ANIME -> AnilistType.ANIME
            MANGA -> AnilistType.MANGA
        }
    }

    fun AnilistType.toDomain(): MediaType {
        return when(this) {
            AnilistType.ANIME -> ANIME
            AnilistType.MANGA -> MANGA
            AnilistType.UNKNOWN__ -> ANIME
        }
    }
}