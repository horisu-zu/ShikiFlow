package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.fragment.ALRelatedMediaShort
import com.example.graphql.shikimori.fragment.RelatedMediaShort
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaTitleMapper.toDomainTitle
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
                    title = anime.relatedAnimeShort.name.toDomainTitle(
                        anime.relatedAnimeShort.english,
                        anime.relatedAnimeShort.russian,
                        null
                    ),
                    coverImageUrl = anime.relatedAnimeShort.poster?.mainUrl ?: "",
                    mediaType = MediaType.ANIME,
                    mediaFormat = anime.relatedAnimeShort.kind?.toDomain() ?: MediaFormat.UNKNOWN,
                    relationKind = relationKind.toDomain()
                )
            }
            false -> {
                RelatedMedia(
                    id = manga?.relatedMangaShort?.id?.toInt() ?: 0,
                    title = (manga?.relatedMangaShort?.name ?: "").toDomainTitle(
                        manga?.relatedMangaShort?.english,
                        manga?.relatedMangaShort?.russian,
                        null
                    ),
                    coverImageUrl = manga?.relatedMangaShort?.poster?.mainUrl ?: "",
                    mediaType = MediaType.MANGA,
                    mediaFormat = manga?.relatedMangaShort?.kind?.toDomain() ?: MediaFormat.UNKNOWN,
                    relationKind = relationKind.toDomain()
                )
            }
        }
    }

    fun ALRelatedMediaShort.toDomain(): RelatedMedia {
        return RelatedMedia(
            id = node?.id ?: 0,
            title = node?.title?.mediaTitle.toDomainTitle(),
            coverImageUrl = node?.coverImage?.large ?: "",
            mediaType = node?.type?.toDomain() ?: MediaType.ANIME,
            mediaFormat = node?.format?.toDomain() ?: MediaFormat.UNKNOWN,
            relationKind = relationType?.toDomain() ?: RelationKind.UNKNOWN
        )
    }
}