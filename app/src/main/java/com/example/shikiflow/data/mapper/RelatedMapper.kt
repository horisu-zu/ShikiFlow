package com.example.shikiflow.data.mapper

import com.example.graphql.AnimeDetailsQuery
import com.example.graphql.MangaDetailsQuery
import com.example.shikiflow.data.common.MediaBasicInfo
import com.example.shikiflow.data.common.MediaRelatedInfo
import com.example.shikiflow.data.common.PosterInfo
import com.example.shikiflow.data.common.RelatedInfo
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapAnimeKind
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapMangaKind
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapRelationKind
import com.example.shikiflow.data.tracks.MediaType

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