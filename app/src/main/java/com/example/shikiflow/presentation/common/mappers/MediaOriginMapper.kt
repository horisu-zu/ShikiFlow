package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.MediaOrigin

object MediaOriginMapper {
    fun MediaOrigin.displayValue(): Int {
        return when(this) {
            MediaOrigin.ORIGINAl -> R.string.anime_origin_original
            MediaOrigin.MANGA -> R.string.anime_origin_manga
            MediaOrigin.WEB_MANGA -> R.string.anime_origin_web_manga
            MediaOrigin.FOUR_KOMA_MANGA -> R.string.anime_origin_4_koma_manga
            MediaOrigin.NOVEL -> R.string.anime_origin_novel
            MediaOrigin.WEB_NOVEL -> R.string.anime_origin_web_novel
            MediaOrigin.VISUAL_NOVEL -> R.string.anime_origin_visual_novel
            MediaOrigin.LIGHT_NOVEL -> R.string.anime_origin_light_novel
            MediaOrigin.GAME -> R.string.anime_origin_game
            MediaOrigin.CARD_GAME -> R.string.anime_origin_card_game
            MediaOrigin.MUSIC -> R.string.anime_origin_music
            MediaOrigin.RADIO -> R.string.anime_origin_radio
            MediaOrigin.BOOK -> R.string.anime_origin_book
            MediaOrigin.PICTURE_BOOK -> R.string.anime_origin_picture_book
            MediaOrigin.MIXED_MEDIA -> R.string.anime_origin_mixed_media
            MediaOrigin.OTHER -> R.string.anime_origin_other
            else -> R.string.common_unknown
        }
    }
}