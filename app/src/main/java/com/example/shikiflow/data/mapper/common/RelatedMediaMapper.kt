package com.example.shikiflow.data.mapper.common

import android.util.Log
import com.example.graphql.anilist.fragment.ALRelatedMediaShort
import com.example.graphql.shikimori.fragment.RelatedMediaShort
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toDomain
import com.example.shikiflow.data.mapper.common.RelationKindMapper.toDomain
import com.example.shikiflow.domain.model.media_details.RelatedMedia
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.track.RelationKind
import com.example.shikiflow.domain.model.tracks.MediaType

object RelatedMediaMapper {

    fun RelatedMediaShort.toDomain(): RelatedMedia {
        val isAnime = this.anime != null

        return when(isAnime) {
            true -> {
                RelatedMedia(
                    id = anime.relatedAnimeShort.id.toInt(),
                    title = anime.relatedAnimeShort.name,
                    coverImageUrl = anime.relatedAnimeShort.poster?.mainUrl ?: "",
                    mediaType = MediaType.ANIME,
                    mediaFormat = anime.relatedAnimeShort.kind?.toDomain() ?: MediaFormat.UNKNOWN,
                    relationKind = relationKind.toDomain()
                )
            }
            false -> {
                RelatedMedia(
                    id = manga?.relatedMangaShort?.id?.toInt() ?: 0,
                    title = manga?.relatedMangaShort?.name ?: "",
                    coverImageUrl = manga?.relatedMangaShort?.poster?.mainUrl ?: "",
                    mediaType = MediaType.MANGA,
                    mediaFormat = manga?.relatedMangaShort?.kind?.toDomain() ?: MediaFormat.UNKNOWN,
                    relationKind = relationKind.toDomain()
                )
            }
        }
    }

    fun ALRelatedMediaShort.toDomain(): RelatedMedia {
        Log.d("RelatedMediaMapper", "MediaShort: $this")
        return RelatedMedia(
            id = this.node?.id ?: 0,
            title = this.node?.title?.romaji ?: "",
            coverImageUrl = this.node?.coverImage?.large ?: "",
            mediaType = this.node?.type?.toDomain() ?: MediaType.ANIME,
            mediaFormat = this.node?.format?.toDomain() ?: MediaFormat.UNKNOWN,
            relationKind = this.relationType?.toDomain() ?: RelationKind.UNKNOWN
        )
    }
}