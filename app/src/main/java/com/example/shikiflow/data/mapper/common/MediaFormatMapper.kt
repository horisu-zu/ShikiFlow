package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.type.MediaFormat as AnilistMediaFormat
import com.example.graphql.shikimori.type.AnimeKindEnum
import com.example.graphql.shikimori.type.MangaKindEnum
import com.example.shikiflow.domain.model.track.MediaFormat

object MediaFormatMapper {
    fun MediaFormat.toShikiAnimeKind(): AnimeKindEnum {
        return when(this) {
            MediaFormat.TV, MediaFormat.TV_SHORT -> AnimeKindEnum.tv
            MediaFormat.MOVIE -> AnimeKindEnum.movie
            MediaFormat.SPECIAL -> AnimeKindEnum.special
            MediaFormat.TV_SPECIAL -> AnimeKindEnum.tv_special
            MediaFormat.OVA -> AnimeKindEnum.ova
            MediaFormat.ONA -> AnimeKindEnum.ona
            MediaFormat.MUSIC -> AnimeKindEnum.music
            MediaFormat.PV -> AnimeKindEnum.pv
            MediaFormat.CM -> AnimeKindEnum.cm
            else -> AnimeKindEnum.UNKNOWN__
        }
    }

    fun MediaFormat.toShikiMangaKind(): MangaKindEnum {
        return when(this) {
            MediaFormat.MANGA -> MangaKindEnum.manga
            MediaFormat.MANHWA -> MangaKindEnum.manhwa
            MediaFormat.MANHUA  -> MangaKindEnum.manhua
            MediaFormat.LIGHT_NOVEL -> MangaKindEnum.light_novel
            MediaFormat.NOVEL -> MangaKindEnum.novel
            MediaFormat.ONE_SHOT -> MangaKindEnum.one_shot
            MediaFormat.DOUJIN -> MangaKindEnum.doujin
            else -> MangaKindEnum.UNKNOWN__
        }
    }

    fun MediaFormat.toAnilistFormat(): AnilistMediaFormat {
        return when(this) {
            MediaFormat.TV -> AnilistMediaFormat.TV
            MediaFormat.TV_SHORT -> AnilistMediaFormat.TV_SHORT
            MediaFormat.MOVIE -> AnilistMediaFormat.MOVIE
            MediaFormat.SPECIAL -> AnilistMediaFormat.SPECIAL
            MediaFormat.OVA -> AnilistMediaFormat.OVA
            MediaFormat.ONA -> AnilistMediaFormat.ONA
            MediaFormat.MUSIC -> AnilistMediaFormat.MUSIC
            MediaFormat.MANGA -> AnilistMediaFormat.MANGA
            MediaFormat.LIGHT_NOVEL -> AnilistMediaFormat.NOVEL
            MediaFormat.ONE_SHOT -> AnilistMediaFormat.ONE_SHOT
            else -> AnilistMediaFormat.UNKNOWN__
        }
    }

    fun AnimeKindEnum.toDomain(): MediaFormat {
        return when(this) {
            AnimeKindEnum.tv ->  MediaFormat.TV
            AnimeKindEnum.movie ->  MediaFormat.MOVIE
            AnimeKindEnum.ova -> MediaFormat.OVA
            AnimeKindEnum.ona ->  MediaFormat.ONA
            AnimeKindEnum.special ->  MediaFormat.SPECIAL
            AnimeKindEnum.tv_special ->  MediaFormat.TV_SPECIAL
            AnimeKindEnum.music ->  MediaFormat.MUSIC
            AnimeKindEnum.pv ->  MediaFormat.PV
            AnimeKindEnum.cm ->  MediaFormat.CM
            else ->  MediaFormat.UNKNOWN
        }
    }

    fun MangaKindEnum.toDomain(): MediaFormat {
        return when(this) {
            MangaKindEnum.manga -> MediaFormat.MANGA
            MangaKindEnum.manhwa -> MediaFormat.MANHWA
            MangaKindEnum.manhua -> MediaFormat.MANHUA
            MangaKindEnum.light_novel -> MediaFormat.LIGHT_NOVEL
            MangaKindEnum.novel -> MediaFormat.NOVEL
            MangaKindEnum.one_shot -> MediaFormat.ONE_SHOT
            MangaKindEnum.doujin -> MediaFormat.DOUJIN
            else -> MediaFormat.UNKNOWN
        }
    }

    fun AnilistMediaFormat.toDomain(): MediaFormat {
        return when(this) {
            AnilistMediaFormat.TV -> MediaFormat.TV
            AnilistMediaFormat.TV_SHORT -> MediaFormat.TV_SHORT
            AnilistMediaFormat.MOVIE -> MediaFormat.MOVIE
            AnilistMediaFormat.SPECIAL -> MediaFormat.SPECIAL
            AnilistMediaFormat.OVA -> MediaFormat.OVA
            AnilistMediaFormat.ONA -> MediaFormat.ONA
            AnilistMediaFormat.MUSIC -> MediaFormat.MUSIC
            AnilistMediaFormat.MANGA -> MediaFormat.MANGA
            AnilistMediaFormat.NOVEL -> MediaFormat.NOVEL
            AnilistMediaFormat.ONE_SHOT -> MediaFormat.ONE_SHOT
            else -> MediaFormat.UNKNOWN
        }
    }
}