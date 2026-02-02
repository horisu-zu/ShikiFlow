package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.type.MediaSource
import com.example.graphql.shikimori.type.AnimeOriginEnum
import com.example.shikiflow.domain.model.media_details.MediaOrigin

object MediaOriginMapper {

    fun AnimeOriginEnum.toDomain(): MediaOrigin {
        return when(this) {
            AnimeOriginEnum.original -> MediaOrigin.ORIGINAl
            AnimeOriginEnum.manga -> MediaOrigin.MANGA
            AnimeOriginEnum.web_manga -> MediaOrigin.WEB_MANGA
            AnimeOriginEnum.four_koma_manga -> MediaOrigin.FOUR_KOMA_MANGA
            AnimeOriginEnum.novel -> MediaOrigin.NOVEL
            AnimeOriginEnum.web_novel -> MediaOrigin.WEB_NOVEL
            AnimeOriginEnum.visual_novel -> MediaOrigin.VISUAL_NOVEL
            AnimeOriginEnum.light_novel -> MediaOrigin.LIGHT_NOVEL
            AnimeOriginEnum.game -> MediaOrigin.GAME
            AnimeOriginEnum.card_game -> MediaOrigin.CARD_GAME
            AnimeOriginEnum.music -> MediaOrigin.MUSIC
            AnimeOriginEnum.radio -> MediaOrigin.RADIO
            AnimeOriginEnum.book -> MediaOrigin.BOOK
            AnimeOriginEnum.picture_book -> MediaOrigin.PICTURE_BOOK
            AnimeOriginEnum.mixed_media -> MediaOrigin.MIXED_MEDIA
            AnimeOriginEnum.other -> MediaOrigin.OTHER
            AnimeOriginEnum.unknown -> MediaOrigin.UNKNOWN
            AnimeOriginEnum.UNKNOWN__ -> MediaOrigin.UNKNOWN
        }
    }

    fun MediaSource.toDomain(): MediaOrigin {
        return when(this) {
            MediaSource.ORIGINAL -> MediaOrigin.ORIGINAl
            MediaSource.MANGA -> MediaOrigin.MANGA
            MediaSource.LIGHT_NOVEL -> MediaOrigin.LIGHT_NOVEL
            MediaSource.VISUAL_NOVEL -> MediaOrigin.VISUAL_NOVEL
            MediaSource.VIDEO_GAME, MediaSource.GAME -> MediaOrigin.GAME
            MediaSource.OTHER -> MediaOrigin.OTHER
            MediaSource.NOVEL -> MediaOrigin.NOVEL
            MediaSource.DOUJINSHI -> MediaOrigin.DOUJIN
            MediaSource.ANIME -> MediaOrigin.ANIME
            MediaSource.WEB_NOVEL -> MediaOrigin.WEB_NOVEL
            MediaSource.LIVE_ACTION -> MediaOrigin.LIVE_ACTION
            MediaSource.COMIC -> MediaOrigin.PICTURE_BOOK
            MediaSource.MULTIMEDIA_PROJECT -> MediaOrigin.MIXED_MEDIA
            MediaSource.PICTURE_BOOK -> MediaOrigin.PICTURE_BOOK
            MediaSource.UNKNOWN__ -> MediaOrigin.UNKNOWN
        }
    }
}