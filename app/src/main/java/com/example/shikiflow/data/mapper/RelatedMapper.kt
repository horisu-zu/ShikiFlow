package com.example.shikiflow.data.mapper

import com.example.graphql.AnimeDetailsQuery
import com.example.graphql.MangaDetailsQuery
import com.example.shikiflow.data.common.AnimeBasicInfo
import com.example.shikiflow.data.common.AnimeRelatedInfo
import com.example.shikiflow.data.common.MangaBasicInfo
import com.example.shikiflow.data.common.MangaRelatedInfo
import com.example.shikiflow.data.common.PosterInfo
import com.example.shikiflow.data.common.RelatedInfo
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapAnimeKind
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapMangaKind
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapRelationKind

object RelatedMapper {
    fun fromAnimeRelated(related: AnimeDetailsQuery.Related): RelatedInfo {
        return AnimeRelatedInfo(
            relationKind = mapRelationKind(related.relationKind),
            anime = related.anime?.let { anime ->
                AnimeBasicInfo(
                    name = anime.name,
                    kind = mapAnimeKind(anime.kind),
                    poster = anime.poster?.let { poster ->
                        PosterInfo(mainUrl = poster.mainUrl)
                    }
                )
            },
            manga = related.manga?.let { manga ->
                MangaBasicInfo(
                    name = manga.name,
                    kind = mapMangaKind(manga.kind),
                    poster = manga.poster?.let { poster ->
                        PosterInfo(mainUrl = poster.mainUrl)
                    }
                )
            }
        )
    }

    fun fromMangaRelated(related: MangaDetailsQuery.Related): RelatedInfo {
        return MangaRelatedInfo(
            relationKind = mapRelationKind(related.relationKind),
            anime = related.anime?.let { anime ->
                AnimeBasicInfo(
                    name = anime.name,
                    kind = mapAnimeKind(anime.kind),
                    poster = anime.poster?.let { poster ->
                        PosterInfo(mainUrl = poster.mainUrl)
                    }
                )
            },
            manga = related.manga?.let { manga ->
                MangaBasicInfo(
                    name = manga.name,
                    kind = mapMangaKind(manga.kind),
                    poster = manga.poster?.let { poster ->
                        PosterInfo(mainUrl = poster.mainUrl)
                    }
                )
            }
        )
    }
}