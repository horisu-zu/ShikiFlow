package com.example.shikiflow.domain.model.mapper

import com.example.graphql.AnimeDetailsQuery
import com.example.graphql.MangaDetailsQuery
import com.example.shikiflow.domain.model.common.MediaBasicInfo
import com.example.shikiflow.domain.model.common.MediaRelatedInfo
import com.example.shikiflow.domain.model.common.PosterInfo
import com.example.shikiflow.domain.model.common.RelatedInfo
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.mapAnimeKind
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.mapMangaKind
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.mapRelationKind
import com.example.shikiflow.domain.model.tracks.MediaType

object RelatedMapper {
    fun fromAnimeRelated(related: AnimeDetailsQuery.Related): RelatedInfo {
        return MediaRelatedInfo(
            relationKind = mapRelationKind(related.relationKind),
            media = if(related.anime != null) {
                MediaBasicInfo(
                    id = related.anime.id,
                    name = related.anime.name,
                    kind = mapAnimeKind(related.anime.kind),
                    poster = related.anime.poster?.let { poster ->
                        PosterInfo(mainUrl = poster.mainUrl)
                    }, mediaType = MediaType.ANIME
                )
            } else {
                related.manga?.let { manga ->
                    MediaBasicInfo(
                        id = manga.id,
                        name = manga.name,
                        kind = mapMangaKind(manga.kind),
                        poster = manga.poster?.let { poster ->
                            PosterInfo(mainUrl = poster.mainUrl)
                        }, mediaType = MediaType.MANGA
                    )
                }
            }
        )
    }

    fun fromMangaRelated(related: MangaDetailsQuery.Related): RelatedInfo {
        return MediaRelatedInfo(
            relationKind = mapRelationKind(related.relationKind),
            media = if(related.anime != null) {
                MediaBasicInfo(
                    id = related.anime.id,
                    name = related.anime.name,
                    kind = mapAnimeKind(related.anime.kind),
                    poster = related.anime.poster?.let { poster ->
                        PosterInfo(mainUrl = poster.mainUrl)
                    }, mediaType = MediaType.ANIME
                )
            } else {
                related.manga?.let { manga ->
                    MediaBasicInfo(
                        id = manga.id,
                        name = manga.name,
                        kind = mapMangaKind(manga.kind),
                        poster = manga.poster?.let { poster ->
                            PosterInfo(mainUrl = poster.mainUrl)
                        }, mediaType = MediaType.MANGA
                    )
                }
            }
        )
    }
}